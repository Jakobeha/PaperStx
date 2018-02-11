package paperstx.components

import japgolly.scalajs.react.{Callback, ScalaComponent, ReactDragEventFromHtml}
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model._

import scalacss.ScalaCssReact._

object TemplateComponent {
  case class Props(
      typedTemplate: TypedTemplate.Full,
      onTemplateChange: TypedTemplate.Full => Callback,
      onDragStart: (ReactDragEventFromHtml, TypedTemplate.Full) => Callback)

  val component =
    ScalaComponent
      .builder[Props]("SingleOverview")
      .render_P { props =>
        val typedTemplate = props.typedTemplate
        val onTemplateChange = props.onTemplateChange
        val onDragStart = props.onDragStart
        <.div(
          paperstx.Styles.singleOverview,
          BlockComponent.apply(typedTemplate, onTemplateChange, onDragStart))
      }
      .build

  def apply(template: TypedTemplate.Full,
            onTemplateChange: TypedTemplate.Full => Callback,
            onDragStart: (ReactDragEventFromHtml,
                          TypedTemplate.Full) => Callback): VdomElement =
    component(Props(template, onTemplateChange, onDragStart))
}
