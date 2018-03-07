package paperstx.model.block

/** Contains blocks for an AST of a programming language. */
case class Language(classes: Seq[BlockClass]) {
  lazy val enumClasses: Seq[EnumBlockClass] = classes.collect {
    case enumClass: EnumBlockClass => enumClass
  }

  lazy val nonEnumClasses: Seq[BlockClass] =
    classes.filter(!_.isInstanceOf[EnumBlockClass])

  lazy val scope: Scope = Scope.absolute(classes.map(_.typ))

  def setEnumClasses(newEnumClasses: Seq[EnumBlockClass]): Language =
    Language(classes = newEnumClasses ++ nonEnumClasses)

  /** All the blocks which belong to the specific type. */
  def blocksForType(typ: BlockType): Seq[TypedBlock] =
    enumClasses
      .filter(clazz => typ.contains(clazz.enumType))
      .flatMap(_.typedBlocks)
}

object Language {

  /** Contains no blocks. */
  val empty: Language = Language(classes = Seq.empty)
}
