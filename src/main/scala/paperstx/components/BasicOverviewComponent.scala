package paperstx.components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model._
import scalacss.ScalaCssReact._

object BasicOverviewComponent {
  val component =
    ScalaComponent
      .builder[Seq[TypedTemplate.Full]]("BasicOverview")
      .render_P { templates =>
        <.div(
          paperstx.Styles.basicOverview,
          templates.toTagMod(SingleOverviewComponent.apply)
        )
      }
      .build

  def apply(templates: Seq[TypedTemplate.Full]): VdomElement =
    component(templates)
}
