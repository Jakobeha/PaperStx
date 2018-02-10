package paperstx.model

import scala.collection.generic.CanBuildFrom
import scalacss.internal.ValueT.Color

sealed trait TemplateClass[TTyp, TTemp, TColor] {

}

case class EnumTemplateClass[TTyp, TTemp, TColor](enumType: EnumTemplateType[TColor],
                                                  templates: Set[Template[TTyp, TTemp, TColor]]) extends TemplateClass[TTyp, TTemp, TColor] {

}

case class UnionTemplateClass[TTyp, TTemp, TColor](label: String,
                                                   subTypes: Set[TTyp]) extends TemplateClass[TTyp, TTemp, TColor] {

}

object TemplateClass {
  implicit class FullTypeTemplateClass[TTemp, TColor](self: TemplateClass[TemplateType[TColor], TTemp, TColor]) {
    val typ: TemplateType[TColor] = self match {
      case EnumTemplateClass(enumType, _) => TemplateType.lift(enumType)
      case UnionTemplateClass(label, subTypes) => TemplateType.union(label, subTypes)
    }
  }

  type Full = TemplateClass[TemplateType.Full, Template.Full, Color]

  def partition[TTyp, TTemp, TColor, That](elems: Traversable[TemplateClass[TTyp, TTemp, TColor]])
                                          (implicit builder: CanBuildFrom[Traversable[TemplateClass[TTyp, TTemp, TColor]], TemplateClass[TTyp, TTemp, TColor], That]): (That, That) =
    (elems.collect { case enumClass: EnumTemplateClass[TTyp @unchecked, TTemp @unchecked, TColor @unchecked] => enumClass },
      elems.collect { case unionClass: UnionTemplateClass[TTyp @unchecked, TTemp @unchecked, TColor @unchecked] => unionClass })
}