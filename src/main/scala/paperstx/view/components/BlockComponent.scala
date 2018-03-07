package paperstx.view.components

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.ScalaComponent
import paperstx.model.block.{Block, BlockFrag, TypedBlock}
import paperstx.view.props.{PhysProps, Prop}

import scalacss.ScalaCssReact._

object BlockComponent {
  case class Props(typedBlock: Prop[TypedBlock],
                   physProps: PhysProps[TypedBlock])

  val component =
    ScalaComponent
      .builder[Props]("Template")
      .render_P { props =>
        val typedBlock = props.typedBlock
        val physProps = props.physProps
        val depScope = physProps.depScope
        val fullScope = physProps.fullScope
        val typ = typedBlock.get.typ
        val block = typedBlock.narrow(_.block, _.setBlock)

        def rewrapBlock(newFrags: Seq[BlockFrag]): TypedBlock =
          typedBlock.get.setBlock(Block(newFrags))

        <.div(
          paperstx.Styles.block,
          ^.backgroundColor := typ.color
            .specify(saturation = 0.75f, brightness = 0.75f)
            .toString,
          ^.title := typ.label,
          block.promap(_.frags, Block.apply).sequenceWithRewrap.toTagMod {
            case (blockFrag, rewrapFrags) =>
              def rewrap(newFrag: BlockFrag): TypedBlock =
                rewrapBlock(rewrapFrags(newFrag))

              val fragDepScope = depScope ++
                block.get.justIndivFragScope(blockFrag.get.instanceBind,
                                             fullScope)
              FragComponent(blockFrag,
                            physProps.narrowRescope(fragDepScope, rewrap))
          }
        )
      }
      .build

  def apply(typedBlock: Prop[TypedBlock],
            physProps: PhysProps[TypedBlock]): VdomElement =
    component(Props(typedBlock, physProps))
}
