package paperstx.components

import japgolly.scalajs.react.vdom.html_<^.{^, _}
import japgolly.scalajs.react.{Callback, ReactDragEventFromHtml, ScalaComponent}
import paperstx.model._

import scalacss.ScalaCssReact._

object PhysBlobComponent {
  case class Props(
      physBlob: PhysBlob,
      onPhysBlobChange: PhysBlob => Callback,
      onPhysBlobDragStart: (ReactDragEventFromHtml, PhysBlob) => Callback)

  val component =
    ScalaComponent
      .builder[Props]("Blob")
      .render_P { props =>
        val physBlob = props.physBlob
        val onPhysBlobChange = props.onPhysBlobChange
        val onPhysBlobDragStart = props.onPhysBlobDragStart
        val typedPhysBlob = TypedBlob.undefinedType[Phase.Full](physBlob.blob)

        <.div(
          paperstx.Styles.physBlob,
          ^.top := s"${physBlob.bounds.top}px",
          ^.left := s"${physBlob.bounds.left}px",
          ^.draggable := true,
          ^.onDragStart ==> { event: ReactDragEventFromHtml =>
            onPhysBlobDragStart(event, physBlob)
          },
          BlobComponent(
            typedPhysBlob,
            onBlobChange = { newBlob =>
              onPhysBlobChange(physBlob.copy(blob = newBlob))
            }, //Doesn't care about templates being dragged.
            //Physical blobs have enough information so they can be identified specifically,
            //but templates would need refs or be ambigous.
            onDragStart = { (_, _) =>
              Callback.empty
            }
          )
        )
      }
      .build

  def apply(physBlob: PhysBlob,
            onPhysBlobChange: PhysBlob => Callback,
            onPhysBlobDragStart: (ReactDragEventFromHtml, PhysBlob) => Callback)
    : VdomElement =
    component(Props(physBlob, onPhysBlobChange, onPhysBlobDragStart))
}
