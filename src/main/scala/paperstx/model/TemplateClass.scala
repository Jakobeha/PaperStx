package paperstx.model

import paperstx.util.HueColor

import scalaz.Applicative
import scalaz.Scalaz._
import paperstx.util.TraverseFix._

sealed trait TemplateClass[TPhase <: Phase]
    extends PhaseTransformable[TemplateClass, TPhase] {}

case class EnumTemplateClass[TPhase <: Phase](
    enumType: EnumTemplateType[TPhase#Color],
    templates: Set[Template[TPhase]])
    extends TemplateClass[TPhase]
    with PhaseTransformable[EnumTemplateClass, TPhase] {
  val typedTemplates: Set[TypedTemplate[TPhase]] = templates.map {
    TypedTemplate(enumType, _)
  }

  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
    (enumType.traverseColor(transformer.traverseColor) |@| templates
      .traverseF[F, Template[TNewPhase]] {
        _.traversePhase(transformer)
      })(EnumTemplateClass.apply)
}

case class UnionTemplateClass[TPhase <: Phase](
    label: String,
    subTypes: Set[TPhase#TemplateType])
    extends TemplateClass[TPhase]
    with PhaseTransformable[UnionTemplateClass, TPhase] {
  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
    (label.point[F] |@| subTypes.traverseF(transformer.traverseTemplateType))(
      UnionTemplateClass.apply)
}

object TemplateClass {
  implicit class FullTypeTemplateClass(self: TemplateClass[Phase.Validated]) {
    val typ: TemplateType[Option[HueColor]] = self match {
      case EnumTemplateClass(enumType, _) => TemplateType.lift(enumType)
      case UnionTemplateClass(label, subTypes) =>
        TemplateType.union(label, subTypes)
    }
  }

  type Full = TemplateClass[Phase.Full]

  def partitionCases[TPhase <: Phase](elems: Set[TemplateClass[TPhase]])
    : (Set[EnumTemplateClass[TPhase]], Set[UnionTemplateClass[TPhase]]) =
    (elems.collect {
      case enumClass: EnumTemplateClass[TPhase @unchecked] =>
        enumClass
    }, elems.collect {
      case unionClass: UnionTemplateClass[TPhase @unchecked] =>
        unionClass
    })
}

object EnumTemplateClass {
  type Full = EnumTemplateClass[Phase.Full]
}

object UnionTemplateClass {
  type Full = UnionTemplateClass[Phase.Full]
}
