package paperstx.components

import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.{Callback, ReactDragEventFromHtml, ScalaComponent}
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model._
import paperstx.util.{Rect, Vector2}

import scalacss.ScalaCssReact._

object EditorComponent {
  case class State(language: Language.Full, canvas: Canvas) {
    def overCanvas(f: Canvas => Canvas): State = {
      State(this.language, f(this.canvas))
    }
  }

  class Backend(scope: BackendScope[Language.Full, State]) {
    def setLanguage(newLanguage: Language.Full): Callback = {
      scope.modState { _.copy(language = newLanguage) }
    }

    def setCanvas(newCanvas: Canvas): Callback = {
      scope.modState { _.copy(canvas = newCanvas) }
    }

    def startDrag(event: ReactDragEventFromHtml,
                  block: TypedTemplate.Full): Callback = {
      val templateElem = event.target
      val elemBounds = Rect(templateElem.getBoundingClientRect())
      val newBlob = PhysBlob(elemBounds, TemplateBlob(block))
      val newSelection =
        Selection(newBlob, Vector2(event.clientX, event.clientY))
      scope.modState { _.overCanvas { _.addSelectExpr(newSelection) } }
    }

    def render(state: State): VdomElement = {
      val language = state.language
      val canvas = state.canvas

      <.div(
        paperstx.Styles.editor,
        FullOverviewComponent(language,
                              onLanguageChange = setLanguage,
                              onDragStart = startDrag),
        CanvasComponent(canvas, onCanvasChange = setCanvas)
      )
    }
  }

  object State {
    def empty: State = State(language = Language.empty, canvas = Canvas.empty)
  }

  val component =
    ScalaComponent
    //Don't think this is the "right" way, but it works
    //but I don't see any problems.
      .builder[Language.Full]("Editor")
      .initialStateFromProps { language =>
        State(language, Canvas.empty)
      }
      .renderBackend[Backend]
      .componentWillReceiveProps { self =>
        self.setState(State(self.nextProps, Canvas.empty))
      }
      .build

  def init(langauge: Language.Full): VdomElement = component(langauge)
}
