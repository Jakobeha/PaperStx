package paperstx.model.canvas

import paperstx.model.block.Blob
import paperstx.util.{Rect, Vector2}

/**
  * A [[Blob]] with physical bounds (position and size).
  */
case class PhysBlob(bounds: Rect, blob: Blob.Full) {
  def move(diff: Vector2): PhysBlob = {
    this.copy(bounds = diff.moveRect(this.bounds))
  }
}
