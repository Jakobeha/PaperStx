package paperstx.model.block

import paperstx.model._
import paperstx.model.phase.{Phase, PhaseTransformable, PhaseTransformer}

import scalaz.Applicative
import scalaz.Scalaz._

sealed trait Blob[TPhase <: Phase] extends PhaseTransformable[Blob, TPhase] {
  def overTemplate[TNewPhase <: Phase](
      f: TypedBlock[TPhase] => TypedBlock[TNewPhase]): Blob[TNewPhase]
}

case class FreeBlob[TPhase <: Phase](content: String)
    extends Blob[TPhase]
    with PhaseTransformable[FreeBlob, TPhase] {
  def overTemplate[TNewPhase <: Phase](
      f: TypedBlock[TPhase] => TypedBlock[TNewPhase]): Blob[TNewPhase] =
    FreeBlob[TNewPhase](content)

  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
    FreeBlob[TNewPhase](content).point[F]
}

case class BlockBlob[TPhase <: Phase](content: TypedBlock[TPhase])
    extends Blob[TPhase]
    with PhaseTransformable[BlockBlob, TPhase] {
  def overTemplate[TNewPhase <: Phase](
      f: TypedBlock[TPhase] => TypedBlock[TNewPhase]): Blob[TNewPhase] =
    BlockBlob(f(content))

  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
    content.traversePhase(transformer).map(BlockBlob.apply)
}

object Blob {
  implicit class FullBlob(self: Blob.Full) {

    /**
      * Whether the blob can fill the hole with the given type.
      * Free blobs can fill any hole, template blobs can fill the same
      * holes as their templates.
      */
    def fitsIn(typ: BlockType): Boolean = self match {
      case FreeBlob(_)         => true
      case BlockBlob(template) => template.fitsIn(typ)
    }
  }

  type Full = Blob[Phase.Full]
}
