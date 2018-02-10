package paperstx.model

import paperstx.util.Fix

import scalacss.internal.ValueT.Color
import scalaz._
import scalaz.Scalaz._
import paperstx.util.TraverseFix._

case class Template[TPhase <: Phase](isBinding: Boolean,
                                     frags: Seq[TemplateFrag[TPhase]])
    extends PhaseTransformable[Template, TPhase] {
  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
    (isBinding
      .point[F] |@| frags.traverseF { _.traversePhase(transformer) })(
      Template.apply[TNewPhase] _)
}

object Template {
  type Full = Template[Phase.Full]
}
