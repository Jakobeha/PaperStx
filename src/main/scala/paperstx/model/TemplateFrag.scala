package paperstx.model

import scala.scalajs.js.RegExp
import scalaz.Applicative
import scalaz.Scalaz._

sealed trait TemplateFrag[TPhase <: Phase]
    extends PhaseTransformable[TemplateFrag, TPhase] {}

case class StaticFrag[TPhase <: Phase](text: String)
    extends TemplateFrag[TPhase]
    with PhaseTransformable[StaticFrag, TPhase] {
  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
    StaticFrag[TNewPhase](this.text).point[F]
}

case class FreeTextFrag[TPhase <: Phase](validator: RegExp, text: String)
    extends TemplateFrag[TPhase]
    with PhaseTransformable[FreeTextFrag, TPhase] {
  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
    FreeTextFrag[TNewPhase](this.validator, this.text).point[F]
}

case class Hole[TPhase <: Phase](typ: TPhase#TemplateType,
                                 isBinding: Boolean,
                                 content: Option[Blob[TPhase#TypedTemplate]])
    extends TemplateFrag[TPhase]
    with PhaseTransformable[Hole, TPhase] {
  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) = {
    (transformer.traverseTemplateType(typ) |@| isBinding.point[F] |@| content
      .traverse { _.traverseTemplate(transformer.traverseTemplate) })(
      Hole.apply[TNewPhase])
  }
}

object TemplateFrag {
  type Full = TemplateFrag[Phase.Full]
}

object StaticFrag {
  type Full = StaticFrag[Phase.Full]
}

object FreeTextFrag {
  type Full = FreeTextFrag[Phase.Full]

  /**
    * A free text fragment with no text.
    */
  def empty[TPhase <: Phase](validator: RegExp): FreeTextFrag[TPhase] =
    FreeTextFrag(validator, text = "")
}

object Hole {
  type Full = Hole[Phase.Full]

  /**
    * A hole with no elements.
    */
  def empty[TPhase <: Phase](typ: TPhase#TemplateType,
                             isBinding: Boolean): Hole[TPhase] =
    Hole(typ, isBinding, content = None)
}
