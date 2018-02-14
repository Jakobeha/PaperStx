package paperstx.components

import japgolly.scalajs.react.{Callback, ScalaComponent, ReactDragEventFromHtml}
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model._

import scalacss.ScalaCssReact._

object BasicOverviewComponent {
  case class Props(
      templates: Seq[TypedBlock.Full],
      onTemplatesChange: Seq[TypedBlock.Full] => Callback,
      onDragStart: (ReactDragEventFromHtml, TypedBlock.Full) => Callback)

  val component =
    ScalaComponent
      .builder[Props]("BasicOverview")
      .render_P { props =>
        val templates = props.templates
        val onTemplatesChange = props.onTemplatesChange
        val onDragStart = props.onDragStart

        <.div(
          paperstx.Styles.basicOverview,
          templates.zipWithIndex.toTagMod {
            case (template, idx) =>
              TemplateComponent(template, onTemplateChange = { newTemplate =>
                onTemplatesChange(templates.updated(idx, newTemplate))
              }, onDragStart)
          }
        )
      }
      .build

  def apply(templates: Seq[TypedBlock.Full],
            onTemplatesChange: Seq[TypedBlock.Full] => Callback,
            onDragStart: (ReactDragEventFromHtml, TypedBlock.Full) => Callback)
    : VdomElement =
    component(Props(templates, onTemplatesChange, onDragStart))
}
