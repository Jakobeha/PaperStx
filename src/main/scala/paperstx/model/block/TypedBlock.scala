package paperstx.model.block

import paperstx.model.phase.{Phase, PhaseTransformable, PhaseTransformer}
import paperstx.model.{PhaseTransformable, PhaseTransformer}

import scalaz.Applicative
import scalaz.Scalaz._

case class TypedBlock[TPhase <: Phase](typ: EnumBlockType, block: Block[TPhase])
    extends PhaseTransformable[TypedBlock, TPhase] {
  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
    (typ.point[F] |@| transformer.traverseBlock(block))(TypedBlock.apply)
}

object TypedBlock {
  implicit class FullTypedTemplate(private val self: TypedBlock.Full) {

    /**
      * Whether the template can fill the hole with the given type.
      */
    def fitsIn(typ: BlockType): Boolean = typ.isSuperset(self.typ)
  }

  type Full = TypedBlock[Phase.Full]
}
