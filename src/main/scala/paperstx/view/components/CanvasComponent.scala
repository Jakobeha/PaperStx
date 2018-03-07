package paperstx.view.components

import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{
  Callback,
  CallbackTo,
  ReactMouseEventFromHtml,
  ScalaComponent
}
import org.scalajs.dom.html
import paperstx.model.block.{Blob, FreeBlob, Scope}
import paperstx.model.physical._
import paperstx.util._
import paperstx.view.props.Prop

import scala.scalajs.js.Dynamic.global
import scalacss.ScalaCssReact._

object CanvasComponent {
  case class Props(canvas: Prop[Canvas], scope: Scope, solidify: String => Blob)

  case class State(curUid: Int) {
    def getUid: (Int, State) = (curUid, useUid)

    def useUid: State = State(this.curUid + 1)
  }

  class Backend(scope: BackendScope[Props, State]) {
    def getUid: CallbackTo[Int] =
      for (curUid <- scope.state.map(_.curUid);
           _ <- scope.setState(State(curUid + 1))) yield curUid

    def render(props: Props) = {
      val canvas = props.canvas
      val scope = props.scope
      val solidify = props.solidify
      val otherExprs = canvas.get.otherExprs

      def createFreeBlob(event: ReactMouseEventFromHtml): Callback = {
        if (event.target != event.currentTarget) {
          //Clicking on sub-element - ignore
          Callback.empty
        } else {
          val newBlob = FreeBlob.empty
          val newBlobBounds = Rect(left = event.clientX,
                                   top = event.clientY,
                                   width = 0,
                                   height = 0)
          getUid >>= { uid =>
            val newPhysBlob = PhysBlob(uid, newBlobBounds, newBlob)
            canvas.modify(_.addExpr(newPhysBlob))
          }
        }
      }

      def tryFillHole(selectedBlob: Blob)(replaceBlobWith: PhysBlob => Canvas,
                                          holeOp: HoleOp[PhysBlob]): Callback =
        holeOp match {
          case EmptyHole(_, _, _) => Callback.empty //Can't empty with selection
          case FillHole(fill) =>
            fill(selectedBlob) match {
              case None => Callback.empty //Couldn't fill.
              case Some(filledBlob) =>
                val newCanvas =
                  replaceBlobWith(filledBlob) //Adds inner blob to outer blob
                  .withoutSelection //Removes (extra) inner blob
                canvas.set(newCanvas)
            }
        }

      def tryEmptyHole(replaceBlobWith: PhysBlob => Canvas,
                       holeOp: HoleOp[PhysBlob]): Callback = holeOp match {
        case FillHole(_) =>
          Callback.empty //Can't fill without selection
        case EmptyHole(emptyCon, emptyEvent, goneBlob) =>
          val roughGoneDOM = emptyEvent.currentTarget
          val roughGoneBounds =
            Rect(roughGoneDOM.getBoundingClientRect())
          getUid >>= { uid =>
            val gonePhysBlob = PhysBlob(uid, roughGoneBounds, goneBlob)
            val selectionPos =
              Vector2(emptyEvent.clientX, emptyEvent.clientY)
            val newSelection = Selection(gonePhysBlob, selectionPos)

            val newCanvas =
              replaceBlobWith(emptyCon) //Removes inner from outer
                .addSelectExpr(newSelection) //Adds inner standalone
            canvas.set(newCanvas) >> emptyEvent.stopPropagationCB
          }
      }

      def otherExprsRendered(
          onFillOrEmpty: (PhysBlob => Canvas, HoleOp[PhysBlob]) => Callback) =
        otherExprs.zipWithIndex
          .map {
            case (physBlob, idx) =>
              def replaceBlobWith(newPhysBlob: PhysBlob): Canvas = {
                val newExprs = otherExprs.updated(idx, newPhysBlob)
                canvas.get.copy(otherExprs = newExprs)
              }

              val removeBlob = {
                val newExprs = otherExprs.patch(idx, Nil, 1)
                canvas.modify(_.copy(otherExprs = newExprs))
              }

              PhysBlobComponent(
                Prop(physBlob, { newPhysBlob =>
                  canvas.set(replaceBlobWith(newPhysBlob))
                }),
                isSelected = false,
                scope = scope,
                solidify = solidify,
                onPhysBlobDelete = removeBlob,
                onPhysBlobDragStart = { (event, physBlob) =>
                  val selection =
                    Selection(physBlob, Vector2(event.clientX, event.clientY))
                  canvas.modify(_.select(selection))
                },
                onFillOrEmpty = onFillOrEmpty(replaceBlobWith, _)
              )
          }
          .reverse
          .toTagMod //Reverses order so latest exprs are on top

      canvas.get.selection match {
        case Some(selection) =>
          val selectedExpr = selection.expr
          val selectedBlob = selectedExpr.blob

          val selectedExprRendered = PhysBlobComponent(
            Prop(selectedExpr, { newSelectedExpr =>
              val newSelection = selection.copy(expr = newSelectedExpr)
              canvas.modify(_.copy(selection = Some(newSelection)))
            }),
            isSelected = true,
            scope = scope,
            solidify = solidify,
            onPhysBlobDelete = Callback.empty, //Can't (and shouldn't) delete selected blob
            onPhysBlobDragStart = (event, selectedBlob) =>
              Callback {
                global.console.warn(
                  s"Selected block 'selected' via drag again: $event | $selectedBlob")
            },
            //Selected elements can't fill - they would fill themselves.
            onFillOrEmpty = { _ =>
              Callback.empty
            }
          )

          <.div(
            paperstx.Styles.canvas,
            ^.`class` := "canvas",
            //Needs to be before selected expression rendered so selected is on top.
            otherExprsRendered(tryFillHole(selectedBlob)),
            selectedExprRendered
          )
        case None =>
          <.div(
            paperstx.Styles.canvas,
            ^.`class` := "canvas",
            ^.onClick ==> createFreeBlob,
            otherExprsRendered(onFillOrEmpty = tryEmptyHole)
          )
      }
    }
  }

  object State {
    val empty: State = State(curUid = 0)
  }

  val component =
    ScalaComponent
      .builder[Props]("Blob")
      .initialState(State.empty)
      .renderBackend[Backend]
      .build

  def apply(canvas: Prop[Canvas],
            scope: Scope,
            solidify: String => Blob): VdomElement =
    component(Props(canvas, scope, solidify))

  private def updateDragPos(canvas: Canvas, onCanvasChange: Canvas => Callback)(
      event: ReactMouseEventFromHtml)
    : Option[CallbackTo[(Selection, ReactMouseEventFromHtml)]] =
    canvas.selection.map { selection =>
      val newSelection =
        selection.moveTo(Vector2(event.clientX, event.clientY))
      val newCanvas = canvas.copy(selection = Some(newSelection))
      onCanvasChange(newCanvas) >> CallbackTo.pure((newSelection, event))
    }

  private def releaseDragFromMove(canvas: Canvas,
                                  onCanvasChange: Canvas => Callback)(
      newSelectionAndEvent: (Selection, ReactMouseEventFromHtml)): Callback = {
    val newSelection = newSelectionAndEvent._1
    val event = newSelectionAndEvent._2

    val selfDOM = event.currentTarget
      .getElementsByClassName("canvas")(0)
      .asInstanceOf[html.Element]
    val mousePos = Vector2(event.clientX, event.clientY)
    val myBounds = Rect(selfDOM.getBoundingClientRect())

    val semiNewCanvas = canvas.copy(selection = Some(newSelection))
    val newCanvas =
      if (mousePos.isInRect(myBounds)) {
        semiNewCanvas.deselect
      } else { //Dragged out
        semiNewCanvas.withoutSelection
      }
    onCanvasChange(newCanvas)
  }

  def onMouseMove(canvas: Canvas, onCanvasChange: Canvas => Callback)(
      event: ReactMouseEventFromHtml): Option[Callback] =
    updateDragPos(canvas, onCanvasChange)(event).map { updateDragPos =>
      updateDragPos >> Callback.empty
    }

  def onMouseUp(canvas: Canvas, onCanvasChange: Canvas => Callback)(
      event: ReactMouseEventFromHtml): Option[Callback] =
    updateDragPos(canvas, onCanvasChange)(event).map { updateDragPos =>
      updateDragPos >>= releaseDragFromMove(canvas, onCanvasChange)
    }
}
