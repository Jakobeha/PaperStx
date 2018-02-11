package paperstx.components

import japgolly.scalajs.react.{Callback, ScalaComponent, ReactDragEventFromHtml}
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model._

import scalacss.ScalaCssReact._

object BlockComponent {
  case class Props(
      typedTemplate: TypedTemplate.Full,
      onTypedTemplateChange: TypedTemplate.Full => Callback,
      onDragStart: (ReactDragEventFromHtml, TypedTemplate.Full) => Callback)

  val component =
    ScalaComponent
      .builder[Props]("Template")
      .render_P { props =>
        val typedTemplate = props.typedTemplate
        val onTypedTemplateChange = props.onTypedTemplateChange
        val onDragStart = props.onDragStart
        val typ = typedTemplate.typ
        val template = typedTemplate.template

        <.div(
          paperstx.Styles.template,
          ^.backgroundColor := typ.color
            .specify(saturation = 0.75f, brightness = 0.5f)
            .toString,
          ^.onDragStart ==> { event =>
            onDragStart(event, typedTemplate)
          },
          template.frags.zipWithIndex.toTagMod {
            case (templateFrag, idx) =>
              FragComponent(
                templateFrag,
                onTemplateFragChange = { newTemplateFrag =>
                  val newFrags = template.frags.updated(idx, newTemplateFrag)
                  val newTemplate = template.copy(frags = newFrags)
                  val newTypedTemplate =
                    typedTemplate.copy[Phase.Full](template = newTemplate)
                  onTypedTemplateChange(newTypedTemplate)
                },
                onDragStart
              )
          }
        )
      }
      .build

  def apply(typedTemplate: TypedTemplate.Full,
            onTypedTemplateChange: TypedTemplate.Full => Callback,
            onDragStart: (ReactDragEventFromHtml,
                          TypedTemplate.Full) => Callback): VdomElement =
    component(Props(typedTemplate, onTypedTemplateChange, onDragStart))
}
