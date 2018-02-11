package paperstx.components

import japgolly.scalajs.react.{Callback, ReactEventFromInput, ScalaComponent}
import japgolly.scalajs.react.vdom.html_<^._

import scala.scalajs.js.RegExp
import scalacss.ScalaCssReact._

object FreeTextComponent {
  case class Props(text: String,
                   validator: Option[RegExp],
                   hasBackground: Boolean,
                   onTextChange: String => Callback) {
    val isValid: Boolean = validator.forall(_.test(text))
  }

  val component =
    ScalaComponent
      .builder[Props]("FreeText")
      .render_P { props =>
        val text = props.text
        val isValid = props.isValid
        val hasBackground = props.hasBackground
        val onTextChange = props.onTextChange

        <.div(
          paperstx.Styles.inlineWrapper,
          <.input.text(
            paperstx.Styles.freeText(isValid, hasBackground),
            ^.width := s"${Math.max(text.length, 1) * 10}px",
            ^.onChange ==> { event: ReactEventFromInput =>
              onTextChange(event.target.value)
            },
            ^.`value` := text
          )
        )
      }
      .build

  def apply(text: String,
            validator: Option[RegExp],
            hasBackground: Boolean,
            onTextChange: String => Callback): VdomElement =
    component(Props(text, validator, hasBackground, onTextChange))
}
