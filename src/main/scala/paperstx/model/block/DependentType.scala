package paperstx.model.block

/** An abstract type which resolves. */
case class DependentType(label: String) {

  /** This type as a property of the instance. */
  def asProperty(instanceBind: String): DependentType =
    DependentType(s"$instanceBind>$label")

  override def toString = label
}

object DependentType {

  /** Makes this type label different than enum types with the same name but different id,
    * and different from all user-defined type labels. */
  def anonymize(label: String, anonId: Int, classId: Int): String =
    s"$label$$$anonId$$$classId"
}
