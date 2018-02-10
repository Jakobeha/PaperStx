package paperstx.components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._
import scalacss.ScalaCssReact._

object Main {
  val component =
    ScalaComponent.builder[Unit]("Main")
      .renderStatic(<.div(
        paperstx.Styles.app,
        Header(),
        Content()))
      .build

  def apply() = component()
}
