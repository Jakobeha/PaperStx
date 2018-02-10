package paperstx.components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model._
import scalacss.ScalaCssReact._

object FullOverviewComponent {
  val component =
    ScalaComponent
      .builder[Language.Full]("FullOverview")
      .render_P { language =>
        <.div(
          paperstx.Styles.fullOverview,
          language.enumClasses.toTagMod(ClassOverviewComponent.apply)
        )
      }
      .build

  def apply(language: Language.Full): VdomElement = component(language)
}
