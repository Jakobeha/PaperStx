package paperstx.components

import japgolly.scalajs.react.vdom.html_<^.{^, _}
import japgolly.scalajs.react.{Callback, ReactDragEventFromHtml, ScalaComponent}
import org.scalajs.dom.{Element, html}
import paperstx.model._
import paperstx.model.block.Blob
import paperstx.model.canvas.{HoleOp, PhysBlob}

import scalacss.ScalaCssReact._

object PhysBlobComponent {
  case class Props(
      physBlob: PhysBlob,
      disablePointers: Boolean,
      onPhysBlobChange: PhysBlob => Callback,
      onPhysBlobDragStart: (ReactDragEventFromHtml, PhysBlob) => Callback,
      onFillOrEmpty: HoleOp[PhysBlob] => Callback)

  val component =
    ScalaComponent
      .builder[Props]("Blob")
      .render_P { props =>
        val physBlob = props.physBlob
        val disablePointers = props.disablePointers
        val onPhysBlobChange = props.onPhysBlobChange
        val onPhysBlobDragStart = props.onPhysBlobDragStart
        val onFillOrEmpty = props.onFillOrEmpty
        val typedPhysBlob =
          OptTypedBlob.undefinedType[Phase.Full](physBlob.blob)

        //Doesn't care about templates being dragged.
        def lift(newBlob: Blob.Full): PhysBlob = physBlob.copy(blob = newBlob)

        <.div(
          paperstx.Styles.physBlob,
          ^.top := s"${physBlob.bounds.top}px",
          ^.left := s"${physBlob.bounds.left}px",
          ^.draggable := true,
          (^.pointerEvents := "none").when(disablePointers),
          ^.onDragStart ==> { event: ReactDragEventFromHtml =>
            onPhysBlobDragStart(event, physBlob)
          },
          BlobComponent(
            typedPhysBlob,
            onPhysBlobChange.compose(lift),
            //Physical blobs have enough information so they can be identified specifically,
            //but templates would need refs or be ambigous.
            onDragStart = { (_, _) =>
              Callback.empty
            },
            onFillOrEmpty = { fillOrEmpty =>
              onFillOrEmpty(fillOrEmpty.overContainer(lift))
            }
          )
        )
      }
      .build

  def apply(physBlob: PhysBlob,
            disablePointers: Boolean,
            onPhysBlobChange: PhysBlob => Callback,
            onPhysBlobDragStart: (ReactDragEventFromHtml, PhysBlob) => Callback,
            onFillOrEmpty: HoleOp[PhysBlob] => Callback): VdomElement =
    component(
      Props(physBlob,
            disablePointers,
            onPhysBlobChange,
            onPhysBlobDragStart,
            onFillOrEmpty))
}
