package paperstx.model.block

/** A type being resolved. */
sealed trait ResolvingType {
  val unresolved: DependentType
  val inputs: Seq[DependentType]
  val outputs: Seq[DependentType]
}

case class ResolvedType(typ: BlockType) extends ResolvingType {
  override val unresolved = typ.unresolved
  override val inputs = typ.inputs
  override val outputs = typ.outputs

  override def toString = typ.toString
}

case class UnresolvedType(typ: RewriteUnionType) extends ResolvingType {
  override val unresolved = typ.unresolved
  override val inputs = typ.inputs
  override val outputs = typ.outputs

  override def toString = typ.toString
}

object ResolvingType {

  /** Contains no sub-types. Technically resolved, but it doesn't matter. */
  def empty(label: String): ResolvingType = ResolvedType(BlockType.empty(label))
}
