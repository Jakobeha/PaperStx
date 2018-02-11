package paperstx.model

import scalaz.Applicative
import scalaz.Scalaz._
import paperstx.util.TraverseFix._

sealed trait TemplateClass[TPhase <: Phase]
    extends PhaseTransformable[TemplateClass, TPhase] {}

case class EnumTemplateClass[TPhase <: Phase](enumType: EnumTemplateType,
                                              templates: Seq[Template[TPhase]])
    extends TemplateClass[TPhase]
    with PhaseTransformable[EnumTemplateClass, TPhase] {
  val typedTemplates: Seq[TypedTemplate[TPhase]] = templates.map {
    TypedTemplate(enumType, _)
  }

  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
    (enumType.point[F] |@| templates
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
  implicit class FullTypeTemplateClass(self: TemplateClass.Full) {
    val typ: TemplateType = self match {
      case EnumTemplateClass(enumType, _) => TemplateType.lift(enumType)
      case UnionTemplateClass(label, subTypes) =>
        TemplateType.union(label, subTypes)
    }
  }

  type Full = TemplateClass[Phase.Full]

  def partitionCases[TPhase <: Phase](elems: Seq[TemplateClass[TPhase]])
    : (Seq[EnumTemplateClass[TPhase]], Seq[UnionTemplateClass[TPhase]]) =
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
