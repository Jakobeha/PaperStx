package paperstx.model

import paperstx.util.HueColor

import scalaz.Scalaz._
import scalaz._
import paperstx.util.TraverseFix._

case class TemplateType(label: String, subTypes: Set[EnumTemplateType]) {
  lazy val colors: Set[HueColor] = subTypes.map { _.color }

  /**
    * Whether this type contains all instances of the other type.
    */
  def isSuperset(other: TemplateType) =
    other.subTypes.subsetOf(this.subTypes)

  /**
    * Whether this type contains all instances of the other type.
    */
  def isSuperset(other: EnumTemplateType) =
    this.subTypes.contains(other)
}

object TemplateType {
  type Full = TemplateType

  /**
    * Labelled undefined and separate from other types, contains no instances.
    */
  val undefined: TemplateType =
    // Won't conflict, because user can't create types with <brackets>
    TemplateType("<undefined>", Set.empty)

  /**
    * Has the same label as the enum type,
    * contains just that as a subtype.
    */
  def lift(enumType: EnumTemplateType): TemplateType =
    TemplateType(enumType.label, Set(enumType))

  /**
    * Contains all instances of the given type.
    */
  def union(label: String, subTypes: Set[TemplateType]): TemplateType =
    TemplateType(label, subTypes.flatMap(_.subTypes))
}
