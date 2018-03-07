package paperstx.util

import org.scalajs.dom.raw.ClientRect

case class Rect(left: Double,
                right: Double,
                top: Double,
                bottom: Double,
                width: Double,
                height: Double) {
  def outset(amount: Double): Rect = {
    Rect(left = this.left - amount,
         right = this.right + amount,
         top = this.top - amount,
         bottom = this.bottom + amount,
         width = this.width + (amount * 2),
         height = this.height + (amount * 2))
  }
}

object Rect {
  def apply(left: Double, top: Double, width: Double, height: Double): Rect =
    Rect(left, right = left + width, top, bottom = top + height, width, height)

  def apply(clientRect: ClientRect): Rect =
    Rect(clientRect.left,
         clientRect.right,
         clientRect.top,
         clientRect.bottom,
         clientRect.width,
         clientRect.height)

  //From https://stackoverflow.com/questions/2752349/fast-rectangle-to-rectangle-intersection
  def rectsIntersect(x: Rect, y: Rect): Boolean = {
    !(y.left > x.right ||
      y.right < x.left ||
      y.top > x.bottom ||
      y.bottom < x.top)
  }
}
