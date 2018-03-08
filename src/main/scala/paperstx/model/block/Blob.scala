package paperstx.model.block

/** Can encode arbitrary text (e.g. a user typing, invalid code), "ideally" encodes a block. */
sealed trait Blob {

  /** Whether the blob can fill a hole with the given (potentially unresolved) type.
    * Free blobs alawys fit, typed blobs fit if the hole's type contains the block type. */
  def fitsIn(holeType: BlockType): Boolean
}

case class FreeBlob(text: String) extends Blob {
  override def fitsIn(holeType: BlockType) = true
}

case class BlockBlob(block: TypedBlock) extends Blob {
  override def fitsIn(holeType: BlockType) = holeType.contains(block.typ)
}

object FreeBlob {
  val empty: FreeBlob = FreeBlob(text = "")
}
