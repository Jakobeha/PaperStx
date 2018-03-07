package paperstx.view.components

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.ScalaComponent
import paperstx.model.block.TypedBlock
import paperstx.view.props.{Prop, TemplateProps}

import scalacss.ScalaCssReact._

object BasicOverviewComponent {
  case class Props(templates: Prop[Seq[TypedBlock]],
                   templateProps: TemplateProps)

  val component =
    ScalaComponent
      .builder[Props]("BasicOverview")
      .render_P { props =>
        val templates = props.templates
        val templateProps = props.templateProps

        <.div(
          paperstx.Styles.basicOverview,
          templates.sequence.toTagMod(TemplateComponent(_, templateProps))
        )
      }
      .build

  def apply(templates: Prop[Seq[TypedBlock]],
            templateProps: TemplateProps): VdomElement =
    component(Props(templates, templateProps))
}
