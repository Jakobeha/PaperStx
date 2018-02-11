package paperstx.components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model.Canvas

import scalacss.ScalaCssReact._

object CanvasComponent {
  val component =
    ScalaComponent
      .builder[Canvas]("Blob")
      .renderStatic(<.div(paperstx.Styles.canvas))
      .build

  def apply(canvas: Canvas): VdomElement = component(canvas)
}
