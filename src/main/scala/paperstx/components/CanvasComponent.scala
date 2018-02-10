package paperstx.components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._
import scalacss.ScalaCssReact._

object CanvasComponent {
  val component =
    ScalaComponent
      .builder[Unit]("Blob")
      .renderStatic(<.div(paperstx.Styles.canvas))
      .build

  def apply(): VdomElement = component()
}
