package paperstx.model.block

import scala.scalajs.js.RegExp

/** A part of a block. */
sealed trait BlockFrag {
  val instanceBind: Option[String]
  val subRewriteBlock: Option[RewriteBlock]

  /** The outputs which would be inside of the frag's instance, local to the frag. */
  protected def subOutputs(scope: Scope): Seq[DependentType]

  /** Contains the types of the outputs which would be inside of the frag's instance, local to the frag.
    * Types can be fully resolved with the parent scope, but for some properties (e.g. output) this is a waste. */
  protected def subScope(parentScope: Scope): Scope

  /** Transform's the fragment's type itself - not sub types. */
  def overType(
      f: FunctionType[DependentType] => FunctionType[DependentType]): BlockFrag

  /** All the blocks satisfying dependent types within the block. */
  def bindBlocks(scope: Scope): Seq[TypedBlock]

  /** The outputs which are inside of the frag's instance, local to the enclosing block. */
  def instanceOutputs(scope: String => Scope): Seq[DependentType] =
    instanceBind match {
      case None => Seq.empty
      case Some(instanceBind_) =>
        subOutputs(scope(instanceBind_)).map(_.asProperty(instanceBind_))
    }

  /** Contains the types of the outputs which are inside of the frag's instance, local to the enclosing block.
    * Types can be fully resolved with the parent scope, but for some properties (e.g. output) this is a waste. */
  def instanceScope(parentScope: String => Scope): Scope =
    instanceBind match {
      case None => Scope.empty
      case Some(_instanceBind) =>
        subScope(parentScope(_instanceBind))
          .mapInputs(_.asProperty(_instanceBind))
    }

  /** Uses the given function to get the block scope, returns the scope outside the frag. */
  def subBlockScope(rootScope: Scope): Option[Scope] =
    instanceBind.flatMap { _instanceBind =>
      subRewriteBlock.map { _subRewriteBlock =>
        _subRewriteBlock
          .justFullFragScope(rootScope)
          .mapInputs(_.asProperty(_instanceBind))
      }
    }
}

/** Immutable - e.g. keywords, core syntax. */
case class StaticFrag(text: String) extends BlockFrag {
  override val instanceBind = None
  override val subRewriteBlock = None

  override protected def subOutputs(scope: Scope) = Seq.empty

  override protected def subScope(parentScope: Scope) = Scope.empty

  override def overType(
      f: FunctionType[DependentType] => FunctionType[DependentType]) = this

  override def bindBlocks(scope: Scope) = Seq.empty
}

/** Mutable - e.g. bind variable names. */
case class FreeTextHole(text: String,
                        validator: RegExp,
                        instanceBind: Option[String])
    extends BlockFrag {
  override val subRewriteBlock = None

  private val bindBlock: Block = Block(Seq(StaticFrag(text)))

  private val bindRewriteBlock: RewriteBlock =
    RewriteBlock(bindBlock, outputs = Rewrite.empty)

  private val bindTypedBlock: TypedBlock =
    TypedBlock(bindRewriteBlock, FreeTextHole.rawType(text))

  override protected def subOutputs(scope: Scope) =
    Seq(FreeTextHole.rawDependentType)

  override protected def subScope(parentScope: Scope) =
    Scope.simple(
      Map(
        FreeTextHole.rawDependentType -> BlockType.pure(
          FreeTextHole.rawType(text))))

  override def overType(
      f: FunctionType[DependentType] => FunctionType[DependentType]) = this

  override def bindBlocks(scope: Scope) = Seq(bindTypedBlock)
}

/** Mutable - e.g. reference variable names. */
case class BlockHole(content: Option[Blob],
                     typ: FunctionType[DependentType],
                     instanceBind: Option[String])
    extends BlockFrag {
  override val subRewriteBlock = content.collect {
    case BlockBlob(typedBlock) => typedBlock.rewriteBlock
  }

  override protected def subOutputs(scope: Scope) =
    scope.semiResolve(typ) match {
      case None            => Seq.empty
      case Some(blockType) => blockType.outputs
    }

  override protected def subScope(parentScope: Scope) = content match {
    case None              => Scope.empty
    case Some(FreeBlob(_)) => Scope.empty
    case Some(BlockBlob(typedBlock)) =>
      typedBlock.rewriteBlock.localScope(parentScope)
  }

  override def overType(
      f: FunctionType[DependentType] => FunctionType[DependentType]) =
    this.copy(typ = f(typ))

  override def bindBlocks(scope: Scope) = subRewriteBlock match {
    case None                   => Seq.empty
    case Some(_subRewriteBlock) => _subRewriteBlock.bindBlocks(scope)
  }
}

object FreeTextHole {

  /** References the of a sub-block which contains raw text. */
  val rawDependentType: DependentType = DependentType("Raw")

  /** A free text hole with no text. */
  def empty(validator: RegExp, instanceBind: Option[String]): FreeTextHole =
    FreeTextHole(text = "", validator, instanceBind)

  /* TODO Maybe want to use a UID instead? */
  /** The type of the sub-block which contains the raw text. */
  def rawType(text: String): EnumType =
    EnumType(s"Raw$$$text", inputs = Seq.empty, outputs = Seq.empty)
}

object BlockHole {

  /** A block hole with no elements. */
  def empty(typ: FunctionType[DependentType],
            instanceBind: Option[String]): BlockHole =
    BlockHole(content = None, typ, instanceBind)
}
