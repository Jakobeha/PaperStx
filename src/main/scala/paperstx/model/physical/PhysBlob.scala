package paperstx.model.physical

import paperstx.model.block._
import paperstx.util.{Rect, Vector2}

/**
  * A block with physical properties - a uid and bounds (position and size).
  */
case class PhysBlob(uid: Int, bounds: Rect, blob: Blob) {
  val instance: String = s"#$uid"

  /** The dependent scope for the block inside blob, if it exists (otherwise an empty scope). */
  def depScope(scope: Scope): Scope = blob match {
    case FreeBlob(_) => Scope.empty
    case BlockBlob(typedBlock) =>
      typedBlock.rewriteBlock
        .justFullFragScope(scope)
        .mapInputs(_.asProperty(instance))
  }

  /** The blocks created by the blob, if it exists (otherwise no blocks). */
  def bindBlocks(scope: Scope): Seq[TypedBlock] = blob match {
    case FreeBlob(_)           => Seq.empty
    case BlockBlob(typedBlock) => typedBlock.rewriteBlock.bindBlocks(scope)
  }

  def move(diff: Vector2): PhysBlob = {
    this.copy(bounds = diff.moveRect(this.bounds))
  }
}
