package paperstx.model.physical

import paperstx.util.Vector2

case class Selection(expr: PhysBlob, mousePos: Vector2) {
  def moveTo(newMousePos: Vector2): Selection = {
    val posDiff = newMousePos - this.mousePos
    Selection(expr = this.expr.move(posDiff), mousePos = newMousePos)
  }
}
