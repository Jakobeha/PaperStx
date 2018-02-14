package paperstx.components

import japgolly.scalajs.react.{
  Callback,
  ReactDragEventFromHtml,
  ReactMouseEventFromHtml,
  ScalaComponent
}
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model._
import paperstx.model.block.{Blob, BlockFrag}
import paperstx.model.canvas.{FillHole, HoleOp}

import scalacss.ScalaCssReact._

object FragComponent {
  case class Props(
      templateFrag: BlockFrag.Full,
      onTemplateFragChange: BlockFrag.Full => Callback,
      onDragStart: (ReactDragEventFromHtml, TypedBlock.Full) => Callback,
      onFillOrEmpty: HoleOp[BlockFrag.Full] => Callback)

  val component =
    ScalaComponent
      .builder[Props]("Frag")
      .render_P { props =>
        val templateFrag = props.templateFrag
        val onTemplateFragChange = props.onTemplateFragChange
        val onDragStart = props.onDragStart
        val onFillOrEmpty = props.onFillOrEmpty

        templateFrag match {
          case StaticFrag(text) => <.pre(paperstx.Styles.staticFrag, text)
          case FreeTextFrag(validator, text) =>
            FreeTextComponent(
              text,
              Some(validator),
              hasBackground = true,
              onTextChange = { newFreeText =>
                onTemplateFragChange(FreeTextFrag(validator, newFreeText))
              })
          case Hole(typ, isBinding, content) =>
            content match {
              case None =>
                <.div(
                  paperstx.Styles.emptyHole,
                  ^.title := typ.label + " hole",
                  ^.onMouseEnter --> onFillOrEmpty(FillHole { blob =>
                    if (!blob.fitsIn(HoleSkeleton(typ, isBinding))) {
                      None
                    } else {
                      Some(Hole[Phase.Full](typ, isBinding, Some(blob)))
                    }
                  }),
                  MultiTintComponent(typ.colors.toSeq)
                )
              case Some(blob) =>
                def lift(newBlob: Blob.Full): BlockFrag.Full = {
                  Hole[Phase.Full](typ, isBinding, Some(newBlob))
                }

                <.div(
                  paperstx.Styles.fullHole,
                  ^.onMouseDown ==> { event: ReactMouseEventFromHtml =>
                    onFillOrEmpty(
                      EmptyHole(Hole(typ, isBinding, None), event, blob))
                  },
                  BlobComponent(
                    blob,
                    onTemplateFragChange.compose(lift),
                    onDragStart,
                    HoleOp.conarrow(onFillOrEmpty, lift)
                  )
                )
            }
        }
      }
      .build

  def apply(templateFrag: BlockFrag.Full,
            onTemplateFragChange: BlockFrag.Full => Callback,
            onDragStart: (ReactDragEventFromHtml, TypedBlock.Full) => Callback,
            onFillOrEmpty: HoleOp[BlockFrag.Full] => Callback): VdomElement =
    component(
      Props(templateFrag, onTemplateFragChange, onDragStart, onFillOrEmpty))
}
