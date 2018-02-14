package paperstx.components

import japgolly.scalajs.react.{Callback, ReactDragEventFromHtml, ScalaComponent}
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model._
import paperstx.model.block.EnumBlockClass

import scalacss.ScalaCssReact._

object ClassOverviewComponent {
  case class Props(
      clazz: EnumBlockClass.Full,
      onClassChange: EnumBlockClass.Full => Callback,
      onDragStart: (ReactDragEventFromHtml, TypedBlock.Full) => Callback)

  val component =
    ScalaComponent
      .builder[Props]("ClassOverview")
      .render_P { props =>
        val clazz = props.clazz
        val onClassChange = props.onClassChange
        val onDragStart = props.onDragStart

        <.div(
          paperstx.Styles.classOverview,
          ClassHeaderComponent(clazz.enumType.label),
          <.div(
            paperstx.Styles.classOverviewBody,
            BasicOverviewComponent(
              clazz.typedTemplates,
              onTemplatesChange = { newTypedTemplates =>
                assert(
                  newTypedTemplates.forall { _.typ == clazz.enumType },
                  "Tried to set a class's typed templates to templates whose types aren't in the class.")

                val newTemplates = newTypedTemplates.map { _.template }
                onClassChange(clazz.copy[Phase.Full](templates = newTemplates))
              },
              onDragStart
            )
          )
        )
      }
      .build

  def apply(clazz: EnumBlockClass.Full,
            onClassChange: EnumBlockClass.Full => Callback,
            onDragStart: (ReactDragEventFromHtml, TypedBlock.Full) => Callback)
    : VdomElement =
    component(Props(clazz, onClassChange, onDragStart))
}
