package paperstx.model

import org.scalajs.dom.ext.Color

import scalaz.Functor
import scalaz.Scalaz._
import scalaz._
import paperstx.util.TraverseFix._

case class TemplateType[TColor](label: String,
                                subTypes: Set[EnumTemplateType[TColor]]) {

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
  type Full = TemplateType[Color]

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
