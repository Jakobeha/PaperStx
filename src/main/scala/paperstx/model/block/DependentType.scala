package paperstx.model.block

import paperstx.model.phase.{Phase, PhaseTransformable, PhaseTransformer}

import scalaz.Applicative
import scalaz.Scalaz._

/**
  * A type which might vary based on context, e.g. an instance type `inst>Type`.
  */
trait DependentType[TPhase <: Phase]
    extends PhaseTransformable[DependentType, TPhase] {
  def specify(scope: BlockScope[TPhase]): Option[TPhase#BlockType]
}

/**
  * A dependent type which won't vary based on context.
  */
case class AbsoluteType[TPhase <: Phase](typ: TPhase#BlockType)
    extends DependentType[TPhase]
    with PhaseTransformable[AbsoluteType, TPhase] {
  override def specify(scope: BlockScope[TPhase]) = Some(this.typ)

  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
    transformer.traverseBlockType(typ).map(AbsoluteType.apply)
}

/**
  * A dependent type which will vary based on context.
  */
case class RelativeType[TPhase <: Phase](location: TypeLocation)
    extends DependentType[TPhase]
    with PhaseTransformable[RelativeType, TPhase] {
  override def specify(scope: BlockScope[TPhase]) =
    scope.typeForLocation(this.location)

  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
    RelativeType[TNewPhase](location).point[F]
}

object DependentType {
  type Full = DependentType[Phase.Full]
}
