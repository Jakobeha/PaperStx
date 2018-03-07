package paperstx.view.components

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.ScalaComponent
import paperstx.model.block.{BlockBlob, TypedBlock}
import paperstx.view.props.{Prop, TemplateProps}

import scalacss.ScalaCssReact._

object TemplateComponent {
  case class Props(template: Prop[TypedBlock], templateProps: TemplateProps)

  val component =
    ScalaComponent
      .builder[Props]("SingleOverview")
      .render_P { props =>
        val template = props.template
        val templateProps = props.templateProps
        val onDragStart = templateProps.onDragStart

        <.div(
          paperstx.Styles.singleOverview,
          ^.draggable := true,
          ^.onDragStart ==> { onDragStart(_, BlockBlob(template.get)) },
          BlockComponent(template, templateProps.toPhysProps)
        )
      }
      .build

  def apply(template: Prop[TypedBlock],
            templateProps: TemplateProps): VdomElement =
    component(Props(template, templateProps))
}
