package paperstx.view.components

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.ScalaComponent
import paperstx.model.block.EnumBlockClass
import paperstx.view.props.{Prop, TemplateProps}

import scalacss.ScalaCssReact._

object ClassOverviewComponent {
  case class Props(clazz: Prop[EnumBlockClass], templateProps: TemplateProps)

  val component =
    ScalaComponent
      .builder[Props]("ClassOverview")
      .render_P { props =>
        val clazz = props.clazz
        val templateProps = props.templateProps

        <.div(
          paperstx.Styles.classOverview,
          ClassHeaderComponent(clazz.get.enumType.label),
          BasicOverviewComponent(
            clazz.narrow(_.typedBlocks, _.setTypedBlocks),
            templateProps
          )
        )
      }
      .build

  def apply(clazz: Prop[EnumBlockClass],
            templateProps: TemplateProps): VdomElement =
    component(Props(clazz, templateProps))
}
