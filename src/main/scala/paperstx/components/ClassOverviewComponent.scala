package paperstx.components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model._
import scalacss.ScalaCssReact._

object ClassOverviewComponent {
  val component =
    ScalaComponent
      .builder[EnumTemplateClass.Full]("ClassOverview")
      .render_P { clazz =>
        <.div(
          paperstx.Styles.classOverview,
          ClassHeaderComponent(clazz.enumType.label),
          <.div(
            paperstx.Styles.classOverviewBody,
            BasicOverviewComponent(clazz.typedTemplates.toSeq)
          )
        )
      }
      .build

  def apply(clazz: EnumTemplateClass.Full): VdomElement = component(clazz)
}
