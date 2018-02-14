package paperstx.components

import japgolly.scalajs.react.{Callback, ScalaComponent, ReactDragEventFromHtml}
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model._

import scalacss.ScalaCssReact._

object FullOverviewComponent {
  case class Props(
      language: Language.Full,
      onLanguageChange: Language.Full => Callback,
      onDragStart: (ReactDragEventFromHtml, TypedBlock.Full) => Callback)

  val component =
    ScalaComponent
      .builder[Props]("FullOverview")
      .render_P { props =>
        val language = props.language
        val onLangaugeChange = props.onLanguageChange
        val onDragStart = props.onDragStart
        val enumClasses = language.enumClasses

        <.div(
          paperstx.Styles.fullOverview,
          enumClasses.zipWithIndex.toTagMod {
            case (enumClass, idx) =>
              ClassOverviewComponent(
                enumClass,
                onClassChange = { newEnumClass =>
                  val newEnumClasses = enumClasses.updated(idx, newEnumClass)
                  val newLanguage = language.setEnumClasses(newEnumClasses)
                  onLangaugeChange(newLanguage)
                },
                onDragStart
              )
          }
        )
      }
      .build

  def apply(language: Language.Full,
            onLanguageChange: Language.Full => Callback,
            onDragStart: (ReactDragEventFromHtml, TypedBlock.Full) => Callback)
    : VdomElement =
    component(Props(language, onLanguageChange, onDragStart))
}
