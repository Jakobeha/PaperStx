package paperstx.components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model._
import scalacss.ScalaCssReact._

object EditorComponent {
  val component =
    ScalaComponent
      .builder[Language.Full]("Editor")
      .render_P { language =>
        <.div(
          paperstx.Styles.editor,
          FullOverviewComponent(language),
          CanvasComponent()
        )
      }
      .build

  def apply(language: Language.Full): VdomElement = component(language)
}
