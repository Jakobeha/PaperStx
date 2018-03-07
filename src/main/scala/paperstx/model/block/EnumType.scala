package paperstx.model.block

import paperstx.util.HueColor

/** The type of a single block, concrete. */
case class EnumType(label: String,
                    inputs: Seq[DependentType],
                    outputs: Seq[DependentType]) {
  val color: HueColor = HueColor.hashed(label)
  val unresolved: DependentType = DependentType(label)

  /** Makes this different than enum types with the same name but different id,
    * and different from all user-defined enum types. */
  def anonymize(anonId: Int, classId: Int): EnumType =
    this.copy(label = DependentType.anonymize(label, anonId, classId))

  def appendInputs(newInputs: Seq[DependentType]): EnumType =
    this.copy(inputs = inputs ++ newInputs)

  override def toString = {
    val inputsSummary = inputs.map(_.toString).mkString(", ")
    val outputsSummary = outputs.map(_.toString).mkString(", ")
    s"$label($inputsSummary)[$outputsSummary]"
  }
}
