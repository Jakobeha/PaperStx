package paperstx.model

import paperstx.model.phase.{Phase, PhaseTransformable, PhaseTransformer}

import scalaz.Applicative

package object block {
  type BlockClass[TPhase <: Phase] = GenBlockClass[ClassBody[TPhase]]

  implicit class ExplicitBlockClass[TPhase <: Phase](self: BlockClass[TPhase])
      extends PhaseTransformable[BlockClass, TPhase] {
    override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
        transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
      self.traverseBody { _.traversePhase(transformer) }
  }

  implicit class FullBlockClass(self: BlockClass.Full) {
    val typ: BlockType = self.body match {
      case EnumClassBody(_) =>
        BlockType.lift(EnumBlockType(self.label),
                       self.inPropTypes,
                       self.outPropTypes)
      case UnionClassBody(subTypes) =>
        BlockType.union(self.label,
                        subTypes.map { _.typ },
                        self.inPropTypes,
                        self.outPropTypes)
      case EmptyClassBody() => BlockType.empty(self)
    }
  }

  implicit class FullDependentBlockClass(
      self: BlockClass[Phase.FullDependent]) {
    def specifyType(scope: BlockScope.Full): BlockType =
      self.body match {
        case EnumClassBody(_) =>
          BlockType.lift(EnumBlockType(self.label),
                         self.inPropTypes,
                         self.outPropTypes)
        case UnionClassBody(subTypes) =>
          BlockType.union(self.label,
                          //Treats unresolvable types as empty
                          subTypes.flatMap { _.typ.specify(scope) },
                          self.inPropTypes,
                          self.outPropTypes)
        case EmptyClassBody() => BlockType.empty(self)
      }
  }

  type EnumBlockClass[TPhase <: Phase] = GenBlockClass[EnumClassBody[TPhase]]

  implicit class ExplicitEnumBlockClass[TPhase <: Phase](
      self: EnumBlockClass[TPhase]) {
    val enumType: EnumBlockType = EnumBlockType(self.label)
    val templates = self.body.blocks

    val typedTemplates: Seq[TypedBlock[TPhase]] = this.templates.map {
      TypedBlock(this.enumType, _)
    }
  }

  type UnionBlockClass[TPhase <: Phase] = GenBlockClass[UnionClassBody[TPhase]]

  type EmptyBlockClass[TPhase <: Phase] = GenBlockClass[EmptyClassBody[TPhase]]
}
