package paperstx.view.components

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.ScalaComponent
import paperstx.model.block.{Language, TypedBlock}
import paperstx.view.props.{Prop, TemplateProps}

import scalacss.ScalaCssReact._

object FullOverviewComponent {
  case class Props(language: Prop[Language],
                   bindBlocks: Seq[TypedBlock],
                   templateProps: TemplateProps)

  val component =
    ScalaComponent
      .builder[Props]("FullOverview")
      .render_P { props =>
        val language = props.language
        val bindBlocks = props.bindBlocks
        val templateProps = props.templateProps
        val enumClasses = language.narrow(_.enumClasses, _.setEnumClasses)

        <.div(
          paperstx.Styles.fullOverview,
          enumClasses.sequence.toTagMod { enumClass =>
            ClassOverviewComponent(enumClass, templateProps)
          },
          BasicOverviewComponent(
            Prop.readonly(bindBlocks), // Can't set bind block
            templateProps
          )
        )
      }
      .build

  /**
    * Creates a full overview component.
    * @param language Classes originally provided, which can be modified.
    * @param bindBlocks Blocks created by other blocks, which can't be modified.
    */
  def apply(language: Prop[Language],
            bindBlocks: Seq[TypedBlock],
            templateProps: TemplateProps): VdomElement =
    component(Props(language, bindBlocks, templateProps))
}
