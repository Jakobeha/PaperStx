package paperstx.components

import japgolly.scalajs.react.{Callback, ReactDragEventFromHtml, ScalaComponent}
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model._
import paperstx.model.block.{Blob, FreeBlob, BlockBlob}
import paperstx.model.canvas.HoleOp

import scalacss.ScalaCssReact._

object BlobComponent {
  case class Props(
      blob: Blob.Full,
      onBlobChange: Blob.Full => Callback,
      onDragStart: (ReactDragEventFromHtml, TypedBlock.Full) => Callback,
      onFillOrEmpty: HoleOp[Blob.Full] => Callback)

  val component =
    ScalaComponent
      .builder[Props]("Blob")
      .render_P { props =>
        val blob = props.blob
        val onBlobChange = props.onBlobChange
        val onDragStart = props.onDragStart
        val onFillOrEmpty = props.onFillOrEmpty

        blob match {
          case FreeBlob(content) =>
            // Remember, you can't drag a free blob...
            <.div(
              paperstx.Styles.freeBlob,
              FreeTextComponent(content,
                                validator = None,
                                hasBackground = false,
                                onTextChange = { newText =>
                                  onBlobChange(FreeBlob(newText))
                                })
            )
          case BlockBlob(typedTemplate) =>
            <.div(
              paperstx.Styles.templateBlob,
              BlockComponent(
                typedTemplate,
                onBlobChange.compose(BlockBlob.apply),
                onDragStart,
                HoleOp.conarrow(onFillOrEmpty, BlockBlob.apply)
              )
            )
        }
      }
      .build

  def apply(blob: Blob.Full,
            onBlobChange: Blob.Full => Callback,
            onDragStart: (ReactDragEventFromHtml, TypedBlock.Full) => Callback,
            onFillOrEmpty: HoleOp[Blob.Full] => Callback): VdomElement =
    component(Props(blob, onBlobChange, onDragStart, onFillOrEmpty))
}
