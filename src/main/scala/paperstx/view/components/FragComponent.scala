package paperstx.view.components

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{
  Callback,
  ReactMouseEventFromHtml,
  ScalaComponent
}
import paperstx.model.block._
import paperstx.model.physical.{EmptyHole, FillHole}
import paperstx.view.props.{FreeFragForm, PhysProps, Prop}

import scalacss.ScalaCssReact._

object FragComponent {
  case class Props(blockFrag: Prop[BlockFrag], physProps: PhysProps[BlockFrag])

  val component =
    ScalaComponent
      .builder[Props]("Frag")
      .render_P { props =>
        val blockFrag = props.blockFrag
        val physProps = props.physProps
        val rootScope = physProps.rootScope
        val depScope = physProps.depScope
        val fullScope = physProps.fullScope
        val onFillOrEmpty = physProps.onFillOrEmpty

        blockFrag.get match {
          case StaticFrag(text) => <.pre(paperstx.Styles.staticFrag, text)
          case FreeTextHole(text, validator, instanceBind) =>
            FreeTextComponent(
              Prop(text, { newText =>
                blockFrag.set(FreeTextHole(newText, validator, instanceBind))
              }),
              FreeFragForm(validator),
              onBlur = Callback.empty,
              onEnter = Callback.empty
            )

          case BlockHole(content, typ, instanceBind) =>
            val fragDepScope = typ.inputs.subScope(fullScope)
            val resolvedType = fullScope.resolve(typ)
            val typeTint = resolvedType match {
              case None => EmptyVdom
              case Some(_resolvedType) =>
                MultiTintComponent(_resolvedType.colors)
            }

            content match {
              case None =>
                <.div(
                  paperstx.Styles.emptyHole,
                  ^.title := typ.typ.label + " hole",
                  ^.onMouseEnter --> onFillOrEmpty(FillHole { newBlob =>
                    println("-----")
                    println(s"newBlob: $newBlob)")
                    println(s"typ: $typ")
                    println(s"resolvedType: $resolvedType")
                    println(s"fragDepScope: $fragDepScope")
                    println(s"depScope: $depScope")
                    println(s"rootScope: $rootScope")
                    if (!newBlob.fitsIn(resolvedType)) {
                      None
                    } else {
                      Some(BlockHole(Some(newBlob), typ, instanceBind))
                    }
                  }),
                  ^.onClick --> blockFrag.set(
                    BlockHole(Some(FreeBlob.empty), typ, instanceBind)),
                  typeTint //Will have transparent background if type can't resolve
                )

              case Some(blob) =>
                val emptyHole = BlockHole(None, typ, instanceBind)

                def rewrap(newBlob: Blob): BlockFrag = {
                  BlockHole(Some(newBlob), typ, instanceBind)
                }

                <.div(
                  paperstx.Styles.fullHole,
                  ^.onDragStart ==> { event: ReactMouseEventFromHtml =>
                    onFillOrEmpty(EmptyHole(emptyHole, event, blob))
                  },
                  BlobComponent(
                    Prop(blob, { newBlob =>
                      blockFrag.set(rewrap(newBlob))
                    }),
                    outerType = resolvedType, //Will have transparent background if free and type can't resolve
                    onFreeBlur = { content =>
                      blockFrag.set(emptyHole).when_(content.isEmpty)
                    },
                    physProps.narrowRescope(fragDepScope, rewrap)
                  )
                )
            }
        }
      }
      .build

  def apply(blockFrag: Prop[BlockFrag],
            physProps: PhysProps[BlockFrag]): VdomElement =
    component(Props(blockFrag, physProps))
}
