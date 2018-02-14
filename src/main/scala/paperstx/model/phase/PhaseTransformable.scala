package paperstx.model.phase

import scala.annotation.unchecked.uncheckedVariance
import scalaz.Applicative

/**
  * Has internal values which are initially parsed,
  * but can be transformed.
  */
trait PhaseTransformable[+TSelf[_ <: Phase], TPhase <: Phase] {
  def overPhase[TNewPhase <: Phase](
      transformer: PurePhaseTransformer[TPhase, TNewPhase]): TSelf[TNewPhase] =
    traversePhase(transformer)

  def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F])
    : F[TSelf[TNewPhase] @uncheckedVariance]
}
