package paperstx.model.block

import paperstx.util.HueColor

case class BlockType(label: String,
                     subTypes: Set[EnumBlockType],
                     inPropTypes: Seq[String],
                     outPropTypes: Seq[String]) {
  lazy val colors: Set[HueColor] = subTypes.map { _.color }

  /**
    * Whether this type contains all instances of the other type.
    */
  def isSuperset(other: BlockType) =
    other.subTypes.subsetOf(this.subTypes)

  /**
    * Whether this type contains all instances of the other type.
    */
  def isSuperset(other: EnumBlockType) =
    this.subTypes.contains(other)
}

object BlockType {

  /**
    * Labelled undefined and separate from other types, contains no instances.
    */
  val undefined: BlockType =
    // Won't conflict, because user can't create types with <brackets>
    BlockType("<undefined>",
              subTypes = Set.empty,
              inPropTypes = Seq.empty,
              outPropTypes = Seq.empty)

  /**
    * Has the same label, input property types, and output property types as the enum type,
    * contains just that as a subtype.
    */
  def lift(enumType: EnumBlockType,
           inPropTypes: Seq[String],
           outPropTypes: Seq[String]): BlockType =
    BlockType(enumType.label, Set(enumType), inPropTypes, outPropTypes)

  /**
    * Contains all instances of the given type.
    */
  def union(label: String,
            subTypes: Set[BlockType],
            inPropTypes: Seq[String],
            outPropTypes: Seq[String]): BlockType =
    BlockType(label, subTypes.flatMap { _.subTypes }, inPropTypes, outPropTypes)

  /**
    * Contains no instances. Typically won't have any input or output types.
    */
  def empty(label: String,
            inPropTypes: Seq[String],
            outPropTypes: Seq[String]): BlockType =
    BlockType(label, Set.empty, inPropTypes, outPropTypes)
}
