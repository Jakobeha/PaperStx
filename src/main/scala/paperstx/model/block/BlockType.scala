package paperstx.model.block

import paperstx.util.HueColor

/** The concrete type of a hole - possible concrete types. */
case class BlockType(label: String,
                     subTypes: Seq[EnumType],
                     inputs: Seq[DependentType],
                     outputs: Seq[DependentType]) {

  /** Has the same label - resolves to this in a root scope. */
  val unresolved: DependentType = DependentType(label)
  val subTypesSet: Set[EnumType] = subTypes.toSet
  val inputsSet: Set[DependentType] = inputs.toSet
  val outputsSet: Set[DependentType] = outputs.toSet
  val colors: Seq[HueColor] = subTypes.map(_.color)

  def isSuperType(other: BlockType) = other.subTypesSet.subsetOf(subTypesSet)

  def contains(other: EnumType) = subTypesSet.contains(other)

  override def toString = {
    val inputsSummary = inputs.map(_.toString).mkString(", ")
    val outputsSummary = outputs.map(_.toString).mkString(", ")
    s"$label{ = ${subTypes.mkString(sep = " | ")} }($inputsSummary)[$outputsSummary]"
  }
}

object BlockType {

  /** The colors for an unknown type. */
  val unknownColors: Seq[HueColor] = Seq.empty

  /** Contains no types. */
  def empty(label: String): BlockType =
    BlockType(label,
              subTypes = Seq.empty,
              inputs = Seq.empty,
              outputs = Seq.empty)

  /** Contains just the given type. */
  def pure(subType: EnumType): BlockType =
    BlockType(label = subType.label,
              subTypes = Seq(subType),
              inputs = subType.inputs,
              outputs = subType.outputs)

  /** Contains all the given types (all their sub-types). */
  def union(label: String,
            subTypes: Seq[BlockType],
            inputs: Seq[DependentType],
            outputs: Seq[DependentType]): BlockType =
    BlockType(label, subTypes.flatMap(_.subTypes), inputs, outputs)
}
