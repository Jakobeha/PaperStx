package paperstx.model.local

import paperstx.model.block.{ClassHead, EnumType}

/** Specific information about a class. */
sealed trait LocalClassBody {
  def combine(head: ClassHead): LocalBlockClass
}

case class LocalEnumClassBody(blocks: Seq[LocalBlock]) extends LocalClassBody {
  override def combine(head: ClassHead) =
    LocalEnumBlockClass(EnumType(head.label, head.inputs, head.outputs), blocks)
}

case class LocalUnionClassBody(subTypes: Seq[LocalFunctionType])
    extends LocalClassBody {
  override def combine(head: ClassHead) =
    LocalUnionBlockClass(head.label, subTypes, head.inputs, head.outputs)
}

case object LocalEmptyClassBody extends LocalClassBody {
  override def combine(head: ClassHead) =
    LocalEmptyBlockClass(head.label, head.inputs, head.outputs)
}
