package paperstx.model.local

import paperstx.util.TraverseFix.TraversableSeq

import paperstx.model.block._

sealed trait LocalBlockClass {
  val unresolvedType: DependentType

  def globalize(anonId: Int, scope: Scope): Global[BlockClass]

  /** Creates a rewrite mapping from this class to the globalized version of this class,
    * assuming the globalized version references the transferred types. */
  private def globalMapping(globalSelf: BlockClass,
                            transferred: Seq[DependentType])
    : (DependentType, FunctionType[DependentType]) =
    (unresolvedType,
     FunctionType(globalSelf.typ.unresolved, Rewrite.id(transferred)))

  /** Globalizes and creates a mapping to the global class. */
  def globalizeWithMapping(anonId: Int,
                           blockId: Int,
                           superClassId: Int,
                           scope: Scope,
                           transferred: Seq[DependentType])
    : Global[(BlockClass, (DependentType, FunctionType[DependentType]))] =
    globalize(anonId, scope)
      .map(_.anonymize(blockId, superClassId).appendInputs(transferred))
      .map(globalSelf => (globalSelf, globalMapping(globalSelf, transferred)))
}

case class LocalEnumBlockClass(enumType: EnumType, blocks: Seq[LocalBlock])
    extends LocalBlockClass {
  override val unresolvedType = enumType.unresolved

  override def globalize(anonId: Int, scope: Scope) =
    blocks.zipWithIndex
      .traverseF[Global, RewriteBlock] {
        case (block, idx) => block.globalize(idx, anonId, scope)
      }
      .map(EnumBlockClass(enumType, _))
}

case class LocalUnionBlockClass(label: String,
                                subTypes: Seq[LocalFunctionType],
                                inputs: Seq[DependentType],
                                outputs: Seq[DependentType])
    extends LocalBlockClass {
  override val unresolvedType = DependentType(label)

  override def globalize(anonId: Int, scope: Scope) =
    subTypes.zipWithIndex
      .traverseF[Global, ProFunctionType] {
        case (subType, idx) => subType.globalize(idx, anonId, scope)
      }
      .map(UnionBlockClass(label, _, inputs, outputs))
}

case class LocalEmptyBlockClass(label: String,
                                inputs: Seq[DependentType],
                                outputs: Seq[DependentType])
    extends LocalBlockClass {
  override val unresolvedType = DependentType(label)

  override def globalize(anonId: Int, scope: Scope) =
    Global.pure(EmptyBlockClass(label, inputs, outputs))
}
