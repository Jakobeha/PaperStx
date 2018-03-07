package paperstx.view.components

import japgolly.scalajs.react.vdom.html_<^.{^, _}
import japgolly.scalajs.react.{Callback, ReactDragEventFromHtml, ScalaComponent}
import paperstx.model.block.{Blob, Scope}
import paperstx.model.physical.{HoleOp, PhysBlob}
import paperstx.view.props.{PhysProps, Prop}

import scalacss.ScalaCssReact._

object PhysBlobComponent {
  case class Props(
      physBlob: Prop[PhysBlob],
      scope: Scope,
      isSelected: Boolean,
      solidify: String => Blob,
      onPhysBlobDelete: Callback,
      onPhysBlobDragStart: (ReactDragEventFromHtml, PhysBlob) => Callback,
      onFillOrEmpty: HoleOp[PhysBlob] => Callback)

  val component =
    ScalaComponent
      .builder[Props]("Blob")
      .render_P { props =>
        val physBlob = props.physBlob
        val scope = props.scope
        val isSelected = props.isSelected
        val solidify = props.solidify
        val onPhysBlobDelete = props.onPhysBlobDelete
        val onPhysBlobDragStart = props.onPhysBlobDragStart
        val onFillOrEmpty = props.onFillOrEmpty
        val depScope = physBlob.get.depScope(scope)

        def rewrap(newBlob: Blob): PhysBlob =
          physBlob.get.copy(blob = newBlob)

        <.div(
          paperstx.Styles.physBlob,
          ^.top := s"${physBlob.get.bounds.top}px",
          ^.left := s"${physBlob.get.bounds.left}px",
          ^.draggable := true,
          (^.pointerEvents := "none").when(isSelected),
          ^.onDragStart ==> { event: ReactDragEventFromHtml =>
            onPhysBlobDragStart(event, physBlob.get)
          },
          BlobComponent(
            physBlob.promap(_.blob, rewrap),
            outerType = None,
            onFreeBlur = { content =>
              onPhysBlobDelete.when_(content.isEmpty)
            },
            PhysProps(
              scope,
              depScope,
              solidify,
              //Physical blobs have enough information so they can be identified specifically,
              //but templates would need refs or be ambiguous.
              onDragStart = (_, _) => Callback.empty,
              onFillOrEmpty = HoleOp.narrow(onFillOrEmpty, rewrap)
            )
          )
        )
      }
      .build

  def apply(physBlob: Prop[PhysBlob],
            scope: Scope,
            isSelected: Boolean,
            solidify: String => Blob,
            onPhysBlobDelete: Callback,
            onPhysBlobDragStart: (ReactDragEventFromHtml, PhysBlob) => Callback,
            onFillOrEmpty: HoleOp[PhysBlob] => Callback): VdomElement =
    component(
      Props(physBlob,
            scope,
            isSelected,
            solidify,
            onPhysBlobDelete,
            onPhysBlobDragStart,
            onFillOrEmpty))
}
