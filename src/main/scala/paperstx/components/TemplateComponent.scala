package paperstx.components

import japgolly.scalajs.react.{
  Callback,
  ReactDragEventFromHtml,
  ReactMouseEventFromHtml,
  ScalaComponent
}
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
          BlockComponent.apply(
            typedTemplate,
            onTemplateChange,
            onDragStart,
            //Templates can't be filled or emptied, like they can't be dragged around.
            //This enforces that rule.
            onFillOrEmpty = { _ =>
              Callback.empty
            }
          )
        )
      }
      .build

  def apply(template: TypedTemplate.Full,
            onTemplateChange: TypedTemplate.Full => Callback,
            onDragStart: (ReactDragEventFromHtml,
                          TypedTemplate.Full) => Callback): VdomElement =
    component(Props(template, onTemplateChange, onDragStart))
}
