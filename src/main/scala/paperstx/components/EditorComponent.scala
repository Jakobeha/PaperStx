package paperstx.components

import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.{Callback, ReactDragEventFromHtml, ScalaComponent}
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model._

import scalacss.ScalaCssReact._

object EditorComponent {
  case class State(language: Language.Full, canvas: Canvas)

  class Backend(scope: BackendScope[Language.Full, State]) {
    def setLanguage(newLanguage: Language.Full): Callback = {
      scope.modState { _.copy(language = newLanguage) }
    }

    def startDrag(event: ReactDragEventFromHtml,
                  block: TypedTemplate.Full): Callback = {
      //TODO Implement
      Callback {}
    }

    def render(state: State): VdomElement = {
      val language = state.language
      val canvas = state.canvas

      <.div(
        paperstx.Styles.editor,
        FullOverviewComponent(language,
                              onLanguageChange = setLanguage,
                              onDragStart = startDrag),
        CanvasComponent(canvas)
      )
    }
  }

  object State {
    def empty: State = State(language = Language.empty, canvas = Canvas.empty)
  }

  val component =
    ScalaComponent
    //Don't think setting property is the "right" way,
    //but I can't see any problems besides wasted data
    //and no clear (to me) better way.
      .builder[Language.Full]("Editor")
      .initialStateFromProps { language =>
        State(language, Canvas.empty)
      }
      .renderBackend[Backend]
      .build

  def init(langauge: Language.Full): VdomElement = component(langauge)
}
