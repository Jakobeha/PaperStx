package paperstx.model.phase

import scalaz.Scalaz.Id

trait PurePhaseTransformer[TIn <: Phase, TOut <: Phase]
    extends PhaseTransformer[TIn, TOut, Id]
