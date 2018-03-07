package paperstx.view.components

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ScalaComponent}
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
          <.h1(paperstx.Styles.appTitle, "PaperStx"),
          LangSelectComponent(props.onLangSelect)
        )
      }
      .build

  def apply(
      onLangSelect: Option[BuildValidation[String]] => Callback): VdomElement =
    component(Props(onLangSelect))
}
