package paperstx.components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._
import scalacss.ScalaCssReact._

object Header {
  val component =
    ScalaComponent.builder[Unit]("Header")
      .renderStatic(<.div(
        paperstx.Styles.appHeader,
        <.img(
          paperstx.Styles.appLogo,
          ^.src := "./assets/logo/scala-js-logo.svg"),
        <.img(
          paperstx.Styles.appLogo,
          ^.src := "./assets/logo/react-logo.svg"),
        <.h2("Welcome to Scala.js and React")))
      .build

  def apply() = component()
}
