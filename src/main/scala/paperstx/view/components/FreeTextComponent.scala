package paperstx.view.components

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{
  Callback,
  ReactEventFromInput,
  ReactKeyboardEventFromInput,
  ScalaComponent
}
import paperstx.view.props.{FreeTextForm, Prop}

import scalacss.ScalaCssReact._

object FreeTextComponent {
  case class Props(text: Prop[String],
                   form: FreeTextForm,
                   onEnter: Callback,
                   onBlur: Callback) {
    val isValid: Boolean = form.validator.forall(_.test(text.get))
  }

  val component =
    ScalaComponent
      .builder[Props]("FreeText")
      .render_P { props =>
        val text = props.text
        val form = props.form
        val onEnter = props.onEnter
        val onBlur = props.onBlur
        val isValid = props.isValid

        <.input.text(
          paperstx.Styles.freeText(isValid, form.hasBackground),
          ^.width := s"${Math.max(text.get.length, 1) * 10}px",
          ^.autoFocus := form.autoFocus,
          ^.onChange ==> { event: ReactEventFromInput =>
            text.set(event.target.value)
          },
          ^.onKeyPress ==> { event: ReactKeyboardEventFromInput =>
            onEnter.when_(event.keyCode == 13) //13 is enter key
          },
          ^.onBlur --> onBlur,
          ^.`value` := text.get
        )
      }
      .build

  def apply(text: Prop[String],
            form: FreeTextForm,
            onEnter: Callback,
            onBlur: Callback): VdomElement =
    component(Props(text, form, onEnter, onBlur))
}
