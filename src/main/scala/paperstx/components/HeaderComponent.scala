package paperstx.components

import japgolly.scalajs.react.{Callback, ScalaComponent}
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.builder.BuildValidation

import scalacss.ScalaCssReact._

object HeaderComponent {
  case class Props(onLangSelect: Option[BuildValidation[String]] => Callback) {}

  val component =
    ScalaComponent
      .builder[Props]("Header")
      .render_P { props =>
        <.div(
          paperstx.Styles.appHeader,
          <.img(paperstx.Styles.appLogo,
                ^.src := "images/logo/scala-js-logo.svg"),
          <.img(paperstx.Styles.appLogo, ^.src := "images/logo/react-logo.svg"),
          <.h2("Welcome to PaperStx"),
          LangSelectComponent(props.onLangSelect)
        )
      }
      .build

  def apply(
      onLangSelect: Option[BuildValidation[String]] => Callback): VdomElement =
    component(Props(onLangSelect))
}
