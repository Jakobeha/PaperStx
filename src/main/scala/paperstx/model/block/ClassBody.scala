package paperstx.model.block

import paperstx.model.phase.{Phase, PhaseTransformable, PhaseTransformer}
import paperstx.util.TraverseFix._

import scalaz.Applicative
import scalaz.Scalaz._

sealed trait ClassBody[TPhase <: Phase]
    extends PhaseTransformable[ClassBody, TPhase] {}

case class EnumClassBody[TPhase <: Phase](blocks: Seq[Block[TPhase]])
    extends ClassBody[TPhase]
    with PhaseTransformable[EnumClassBody, TPhase] {
  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
    blocks
      .traverseF[F, Block[TNewPhase]] { _.traversePhase(transformer) }
      .map(EnumClassBody.apply)
}

case class UnionClassBody[TPhase <: Phase](subTypes: Set[ReExportType[TPhase]])
    extends ClassBody[TPhase]
    with PhaseTransformable[UnionClassBody, TPhase] {
  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
    subTypes
      .traverseF { _.traversePhase(transformer) }
      .map(UnionClassBody.apply)
}

case class EmptyClassBody[TPhase <: Phase]()
    extends ClassBody[TPhase]
    with PhaseTransformable[EmptyClassBody, TPhase] {
  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
    EmptyClassBody[TNewPhase]().point[F]
}

object ClassBody {
  type Full = ClassBody[Phase.Full]
}

object EnumClassBody {
  type Full = EnumClassBody[Phase.Full]
}

object UnionClassBody {
  type Full = UnionClassBody[Phase.Full]
}

object EmptyClassBody {
  type Full = EmptyClassBody[Phase.Full]
}
