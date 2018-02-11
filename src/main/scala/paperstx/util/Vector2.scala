package paperstx.util

case class Vector2(x: Double, y: Double) {
  def -(other: Vector2): Vector2 = {
    Vector2(this.x - other.x, this.y - other.y)
  }

  def isInRect(rect: Rect): Boolean = {
    this.x >= rect.left &&
    this.x <= rect.right &&
    this.y >= rect.top &&
    this.y <= rect.bottom
  }

  def moveRect(rect: Rect): Rect = {
    rect.copy(left = rect.left + this.x,
              right = rect.right + this.x,
              top = rect.top + this.y,
              bottom = rect.bottom + this.y)
  }
}
