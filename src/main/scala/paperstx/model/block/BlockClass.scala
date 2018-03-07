package paperstx.model.block

/** Contains many blocks with the same type. */
sealed trait BlockClass {
  val typ: ResolvingType
  val inputs: Seq[DependentType]
  val outputs: Seq[DependentType]

  /** Makes this different than classes with the same name but different ids,
    * and different from all user-defined classes. */
  def anonymize(anonId: Int, classId: Int): BlockClass

  def appendInputs(newInputs: Seq[DependentType]): BlockClass
}

/** Directly contains blocks of the type. */
case class EnumBlockClass(enumType: EnumType, blocks: Seq[RewriteBlock])
    extends BlockClass {
  override val typ = ResolvedType(BlockType.pure(enumType))
  override val inputs: Seq[DependentType] = typ.inputs
  override val outputs: Seq[DependentType] = typ.outputs

  val typedBlocks: Seq[TypedBlock] = blocks.map(TypedBlock(_, enumType))

  /**
    * Constraint: The blocks need to be derived from this class -
    * they need this class's type.
    */
  def setTypedBlocks(newTypedBlocks: Seq[TypedBlock]): EnumBlockClass = {
    assert(
      newTypedBlocks.forall(_.typ == enumType),
      "Tried to set a class's templates to blocks not members of the class."
    )

    this.copy(blocks = newTypedBlocks.map(_.rewriteBlock))
  }

  override def anonymize(anonId: Int, classId: Int) =
    this.copy(enumType = enumType.anonymize(anonId, classId))

  override def appendInputs(newInputs: Seq[DependentType]) =
    this.copy(enumType = enumType.appendInputs(newInputs))

}

/** Indirectly contains blocks of sub-types. */
case class UnionBlockClass(label: String,
                           subTypes: Seq[ProFunctionType],
                           inputs: Seq[DependentType],
                           outputs: Seq[DependentType])
    extends BlockClass {
  override val typ = UnresolvedType(
    RewriteUnionType(label, subTypes, inputs, outputs))

  override def anonymize(anonId: Int, classId: Int) =
    this.copy(label = DependentType.anonymize(label, anonId, classId))

  override def appendInputs(newInputs: Seq[DependentType]) =
    this.copy(inputs = inputs ++ newInputs)
}

/** Contains no blocks of any type. */
case class EmptyBlockClass(label: String,
                           inputs: Seq[DependentType],
                           outputs: Seq[DependentType])
    extends BlockClass {
  override val typ = ResolvedType(BlockType.empty(label))

  override def anonymize(anonId: Int, classId: Int) =
    this.copy(label = DependentType.anonymize(label, anonId, classId))

  override def appendInputs(newInputs: Seq[DependentType]) =
    this.copy(inputs = inputs ++ newInputs)
}
