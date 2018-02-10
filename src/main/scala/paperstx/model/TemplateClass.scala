package paperstx.model

import scala.collection.generic.CanBuildFrom
import scalacss.internal.ValueT.Color

sealed trait TemplateClass[TPhase <: Phase] {}

case class EnumTemplateClass[TPhase <: Phase](
    enumType: EnumTemplateType[TPhase#Color],
    templates: Set[Template[TPhase]])
    extends TemplateClass[TPhase] {}

case class UnionTemplateClass[TPhase <: Phase](
    label: String,
    subTypes: Set[TPhase#TemplateType])
    extends TemplateClass[TPhase] {}

object TemplateClass {
  implicit class FullTypeTemplateClass[TOrig <: Phase](
      self: TemplateClass[Phase.FullType[TOrig]]) {
    val typ: TemplateType[TOrig#Color] = self match {
      case EnumTemplateClass(enumType, _) => TemplateType.lift(enumType)
      case UnionTemplateClass(label, subTypes) =>
        TemplateType.union(label, subTypes)
    }
  }

  type Full = TemplateClass[Phase.Full]

  def partition[TPhase <: Phase, That](
      elems: Traversable[TemplateClass[TPhase]])(
      implicit builder: CanBuildFrom[Traversable[TemplateClass[TPhase]],
                                     TemplateClass[TPhase],
                                     That]): (That, That) =
    (elems.collect {
      case enumClass: EnumTemplateClass[TPhase @unchecked] =>
        enumClass
    }, elems.collect {
      case unionClass: UnionTemplateClass[TPhase @unchecked] =>
        unionClass
    })
}
