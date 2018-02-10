package paperstx.components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model._

import scalacss.ScalaCssReact._

object SingleOverviewComponent {
  val component =
    ScalaComponent
      .builder[TypedTemplate.Full]("SingleOverview")
      .render_P { template =>
        <.div(paperstx.Styles.singleOverview, TemplateComponent.apply(template))
      }
      .build

  def apply(template: TypedTemplate.Full): VdomElement =
    component(template)
}
