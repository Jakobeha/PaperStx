package paperstx.model

import scalaz.Applicative

import scala.annotation.unchecked.uncheckedVariance

/**
  * Has internal values which are initially parsed,
  * but can be transformed.
  */
trait PhaseTransformable[+TSelf[_ <: Phase], TPhase <: Phase] {
  def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F])
    : F[TSelf[TNewPhase] @uncheckedVariance]
}
