package paperstx.view.components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._

import scalacss.ScalaCssReact._

object ErrorMsgComponent {
  val component =
    ScalaComponent
      .builder[Seq[String]]("ErrorMsg")
      .render_P { errors =>
        <.div(
          paperstx.Styles.errorMsg,
          <.div(paperstx.Styles.errorTitle, "Couldn't build language"),
          errors.toTagMod { <.div(paperstx.Styles.errorDetail, _) },
          <.div(paperstx.Styles.errorNote,
                "Note that pstx files have a ",
                <.i("very strict"),
                " syntax - even extra whitspace will make them invalid.")
        )
      }
      .build

  def apply(errors: Seq[String]) = component(errors)
}
