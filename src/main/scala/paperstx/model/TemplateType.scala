package paperstx.model

import paperstx.util.HueColor

import scalaz.Scalaz._
import scalaz._
import paperstx.util.TraverseFix._

case class TemplateType[TColor](label: String,
                                subTypes: Set[EnumTemplateType[TColor]]) {
  lazy val colors: Set[TColor] = subTypes.map { _.color }

  /**
    * Whether this type contains all instances of the other type.
    */
  def isSuperset(other: TemplateType[TColor]) =
    other.subTypes.subsetOf(this.subTypes)

  /**
    * Whether this type contains all instances of the other type.
    */
  def isSuperset(other: EnumTemplateType[TColor]) =
    this.subTypes.contains(other)

  def traverseColor[TNewColor, F[_]: Applicative](
      f: TColor => F[TNewColor]): F[TemplateType[TNewColor]] = {
    subTypes.traverseF { _.traverseColor(f) }.map {
      TemplateType(this.label, _)
    }
  }
}

object TemplateType {
  type Full = TemplateType[HueColor]

  /**
    * Labelled undefined and separate from other types, contains no instances.
    */
  def undefined[TColor]: TemplateType[TColor] =
    // Won't conflict, because user can't create types with <brackets>
    TemplateType("<undefined>", Set.empty)

  /**
    * Has the same label as the enum type,
    * contains just that as a subtype.
    */
  def lift[TColor](enumType: EnumTemplateType[TColor]): TemplateType[TColor] =
    TemplateType(enumType.label, Set(enumType))

  /**
    * Contains all instances of the given type.
    */
  def union[TColor](label: String,
                    subTypes: Set[TemplateType[TColor]]): TemplateType[TColor] =
    TemplateType(label, subTypes.flatMap(_.subTypes))
}
