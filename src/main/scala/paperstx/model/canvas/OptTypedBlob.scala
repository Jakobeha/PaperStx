package paperstx.model.canvas

import paperstx.model.block.{Blob, BlockType}
import paperstx.model.phase.Phase

/**
  * A blob which could have a type.
  */
case class OptTypedBlob[TPhase <: Phase](outerType: Option[BlockType],
                                         blob: Blob[TPhase]) {}

object OptTypedBlob {
  type Full = OptTypedBlob[Phase.Full]

  /**
    * A blob with type information.
    */
  def typed[TPhase <: Phase](outerType: BlockType, blob: Blob[TPhase]) =
    OptTypedBlob(Some(outerType), blob)

  /**
    * A blob with no type information.
    */
  def untyped[TPhase <: Phase](blob: Blob[TPhase]) =
    OptTypedBlob(outerType = None, blob)
}
