package paperstx.model

import paperstx.util.{Rect, Vector2}

/**
  * A [[paperstx.model.Blob]] with physical bounds (position and size).
  */
case class PhysBlob(bounds: Rect, blob: Blob.Full) {
  def move(diff: Vector2): PhysBlob = {
    this.copy(bounds = diff.moveRect(this.bounds))
  }
}
