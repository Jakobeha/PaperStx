package paperstx.components

import japgolly.scalajs.react.{Callback, ReactDragEventFromHtml, ScalaComponent}
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model._
import paperstx.model.block.BlockFrag
import paperstx.model.canvas.HoleOp

import scalacss.ScalaCssReact._

object BlockComponent {
  case class Props(
      typedTemplate: TypedBlock.Full,
      onTypedTemplateChange: TypedBlock.Full => Callback,
      onDragStart: (ReactDragEventFromHtml, TypedBlock.Full) => Callback,
      onFillOrEmpty: HoleOp[TypedBlock.Full] => Callback)

  val component =
    ScalaComponent
      .builder[Props]("Template")
      .render_P { props =>
        val typedTemplate = props.typedTemplate
        val onTypedTemplateChange = props.onTypedTemplateChange
        val onDragStart = props.onDragStart
        val onFillOrEmpty = props.onFillOrEmpty
        val typ = typedTemplate.typ
        val template = typedTemplate.template

        <.div(
          paperstx.Styles.template,
          ^.backgroundColor := typ.color
            .specify(saturation = 0.75f, brightness = 0.75f)
            .toString,
          ^.title := typ.label,
          ^.draggable := true,
          ^.onDragStart ==> { event =>
            onDragStart(event, typedTemplate)
          },
          template.frags.zipWithIndex.toTagMod {
            case (templateFrag, idx) =>
              def lift(newTemplateFrag: BlockFrag.Full): TypedBlock.Full = {
                val newFrags = template.frags.updated(idx, newTemplateFrag)
                val newTemplate = template.copy(frags = newFrags)
                typedTemplate.copy[Phase.Full](template = newTemplate)
              }

              FragComponent(
                templateFrag,
                onTypedTemplateChange.compose(lift),
                onDragStart,
                HoleOp.conarrow(onFillOrEmpty, lift)
              )
          }
        )
      }
      .build

  def apply(typedTemplate: TypedBlock.Full,
            onTypedTemplateChange: TypedBlock.Full => Callback,
            onDragStart: (ReactDragEventFromHtml, TypedBlock.Full) => Callback,
            onFillOrEmpty: HoleOp[TypedBlock.Full] => Callback): VdomElement =
    component(
      Props(typedTemplate, onTypedTemplateChange, onDragStart, onFillOrEmpty))
}
