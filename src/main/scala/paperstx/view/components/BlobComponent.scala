package paperstx.view.components

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ScalaComponent}
import paperstx.model.block._
import paperstx.view.props.{FreeBlobForm, PhysProps, Prop}

import scalacss.ScalaCssReact._

object BlobComponent {
  case class Props(
      blob: Prop[Blob],
      outerType: Option[BlockType],
      transferRewritesByType: Map[EnumType, Rewrite[DependentType]],
      onFreeBlur: String => Callback,
      physProps: PhysProps[Blob])

  val component =
    ScalaComponent
      .builder[Props]("Blob")
      .render_P { props =>
        val blob = props.blob
        val outerType = props.outerType
        val transferRewritesByType = props.transferRewritesByType
        val onFreeBlur = props.onFreeBlur
        val physProps = props.physProps
        val rootScope = physProps.rootScope
        val depScope = physProps.depScope
        val solidify = physProps.solidify
        val onDragStart = physProps.onDragStart

        blob.get match {
          case FreeBlob(content) =>
            val optTint = outerType match {
              case None => EmptyVdom
              case Some(_outerType) =>
                <.div(paperstx.Styles.freeBlobHoleBg,
                      MultiTintComponent(_outerType.colors))
            }

            //Currently, you can't drag a free blob in a hole
            <.div(
              paperstx.Styles.freeBlob,
              ^.onDragStart ==> { onDragStart(_, blob.get) },
              optTint,
              FreeTextComponent(
                Prop(content, { newText =>
                  blob.set(FreeBlob(newText))
                }),
                FreeBlobForm,
                onEnter = blob.set(solidify(content)),
                onFreeBlur(content)
              )
            )

          case BlockBlob(typedBlock) =>
            def rewrap(newTypedBlock: TypedBlock): Blob = {
              BlockBlob(newTypedBlock)
            }

            val transferRewrite = transferRewritesByType.get(typedBlock.typ)
            val blockDepScope = transferRewrite match {
              case None => depScope
              case Some(_transferRewrite) =>
                depScope.rewrite(_transferRewrite, rootScope)
            }

            <.div(
              paperstx.Styles.blockBlob,
              ^.draggable := true,
              ^.onDragStart ==> { onDragStart(_, blob.get) },
              BlockComponent(
                Prop(typedBlock, { newTypedBlock =>
                  blob.set(rewrap(newTypedBlock))
                }),
                physProps.narrowRescope(blockDepScope, rewrap)
              )
            )
        }
      }
      .build

  def apply(blob: Prop[Blob],
            outerType: Option[BlockType],
            transferRewritesByType: Map[EnumType, Rewrite[DependentType]],
            onFreeBlur: String => Callback,
            physProps: PhysProps[Blob]): VdomElement =
    component(
      Props(blob, outerType, transferRewritesByType, onFreeBlur, physProps))
}
