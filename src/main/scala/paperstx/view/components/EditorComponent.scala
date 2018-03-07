package paperstx.view.components

import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ReactDragEventFromHtml, ScalaComponent}
import paperstx.model.block._
import paperstx.model.physical.{Canvas, PhysBlob, Selection}
import paperstx.util.{Rect, Vector2}
import paperstx.view.props.{Prop, TemplateProps}

import scalacss.ScalaCssReact._

object EditorComponent {
  case class State(language: Language, canvas: Canvas) {
    val nextUid: Int = canvas.nextUid

    def overCanvas(f: Canvas => Canvas): State = {
      State(this.language, f(this.canvas))
    }
  }

  class Backend(scope: BackendScope[Language, State]) {
    def setLanguage(newLanguage: Language): Callback = {
      scope.modState { _.copy(language = newLanguage) }
    }

    def setCanvas(newCanvas: Canvas): Callback = {
      scope.modState { _.copy(canvas = newCanvas) }
    }

    def startDrag(event: ReactDragEventFromHtml, blob: Blob): Callback =
      scope.state.flatMap { state =>
        val nextUid = state.nextUid

        val templateElem = event.target
        val elemBounds = Rect(templateElem.getBoundingClientRect())
        val newBlob = PhysBlob(nextUid, elemBounds, blob)
        val newSelection =
          Selection(newBlob, Vector2(event.clientX, event.clientY))
        scope.setState(state.overCanvas(_.addSelectExpr(newSelection))) >>
          event.preventDefaultCB
      }

    def solidify(text: String): Blob = FreeBlob(text)

    def render(state: State): VdomElement = {
      val language = state.language
      val canvas = state.canvas
      val languageProp = Prop(state.language, setLanguage)
      val canvasProp = Prop(state.canvas, setCanvas)
      val bindBlocks = canvas.bindBlocks(language.scope)
      val scope = language.scope

      <.div(
        paperstx.Styles.editor,
        ^.onMouseMove ==>? CanvasComponent
          .onMouseMove(canvas, onCanvasChange = setCanvas),
        ^.onMouseUp ==>? CanvasComponent.onMouseUp(canvas,
                                                   onCanvasChange = setCanvas),
        FullOverviewComponent(
          languageProp,
          bindBlocks,
          TemplateProps(scope, solidify, onDragStart = startDrag)),
        CanvasComponent(canvasProp, scope, solidify)
      )
    }
  }

  val component =
    ScalaComponent
    //Don't think this is the "right" way, but it works
    //but I don't see any problems.
      .builder[Language]("Editor")
      .initialStateFromProps(State(_, Canvas.empty))
      .renderBackend[Backend]
      .componentWillReceiveProps { self =>
        self.setState(State(self.nextProps, Canvas.empty))
      }
      .build

  def init(langauge: Language): VdomElement = component(langauge)
}
