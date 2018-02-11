package paperstx.components

import japgolly.scalajs.react.{Callback, ReactDragEventFromHtml, ScalaComponent}
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model._

import scalacss.ScalaCssReact._

object BlobComponent {
  case class Props(
      blob: TypedBlob.Full,
      onBlobChange: Blob.Full => Callback,
      onDragStart: (ReactDragEventFromHtml, TypedTemplate.Full) => Callback,
      onFillOrEmpty: HoleOp[Blob.Full] => Callback)

  val component =
    ScalaComponent
      .builder[Props]("Blob")
      .render_P { props =>
        val typedBlob = props.blob
        val onBlobChange = props.onBlobChange
        val onDragStart = props.onDragStart
        val onFillOrEmpty = props.onFillOrEmpty
        val outerType = typedBlob.outerType
        val blob = typedBlob.blob

        blob match {
          case FreeBlob(content) =>
            // Remember, you can't drag a free blob...
            <.div(
              paperstx.Styles.freeBlob,
              MultiTintComponent(outerType.colors.toSeq),
              FreeTextComponent(content,
                                validator = None,
                                hasBackground = false,
                                onTextChange = { newText =>
                                  onBlobChange(FreeBlob(newText))
                                })
            )
          case TemplateBlob(typedTemplate) =>
            <.div(
              paperstx.Styles.templateBlob,
              BlockComponent(
                typedTemplate,
                onBlobChange.compose(TemplateBlob.apply),
                onDragStart,
                HoleOp.conarrow(onFillOrEmpty, TemplateBlob.apply)
              )
            )
        }
      }
      .build

  def apply(
      blob: TypedBlob.Full,
      onBlobChange: Blob.Full => Callback,
      onDragStart: (ReactDragEventFromHtml, TypedTemplate.Full) => Callback,
      onFillOrEmpty: HoleOp[Blob.Full] => Callback): VdomElement =
    component(Props(blob, onBlobChange, onDragStart, onFillOrEmpty))
}
