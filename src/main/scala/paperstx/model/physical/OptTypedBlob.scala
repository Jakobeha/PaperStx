package paperstx.model.physical

import paperstx.model.block.{Blob, BlockType}

/**
  * A blob which could be typed.
  */
case class OptTypedBlob(outerType: Option[BlockType], blob: Blob)

object OptTypedBlob {

  /**
    * A blob with type information.
    */
  def typed(outerType: BlockType, blob: Blob) =
    OptTypedBlob(Some(outerType), blob)

  /**
    * A blob with no type information.
    */
  def untyped(blob: Blob) =
    OptTypedBlob(outerType = None, blob)
}
