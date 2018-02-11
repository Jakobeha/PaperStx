package paperstx.components

import japgolly.scalajs.react.{Callback, CallbackTo, ReactMouseEventFromHtml, ScalaComponent}
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.html
import paperstx.model._
import paperstx.util._

import scalajs.js.Dynamic.global
import scalacss.ScalaCssReact._

object CanvasComponent {
  case class Props(canvas: Canvas, onCanvasChange: Canvas => Callback)

  val component =
    ScalaComponent
      .builder[Props]("Blob")
      .render_P { props =>
        val canvas = props.canvas
        val onCanvasChange = props.onCanvasChange
        val otherExprs = canvas.otherExprs

        def otherExprsRendered(
            onFillOrEmpty: (PhysBlob => Canvas, HoleOp[PhysBlob]) => Callback) =
          otherExprs.zipWithIndex
            .map {
              case (physBlob, idx) =>
                def replaceBlobWith(newPhysBlob: PhysBlob): Canvas = {
                  val newExprs = otherExprs.updated(idx, newPhysBlob)
                  canvas.copy(otherExprs = newExprs)
                }

                PhysBlobComponent(
                  physBlob,
                  disablePointers = false,
                  onCanvasChange.compose(replaceBlobWith),
                  onPhysBlobDragStart = { (event, physBlob) =>
                    val selection =
                      Selection(physBlob, Vector2(event.clientX, event.clientY))
                    val newCanvas = canvas.select(selection)
                    onCanvasChange(newCanvas)
                  },
                  { onFillOrEmpty(replaceBlobWith, _) }
                )
            }
            .reverse
            .toTagMod //Reverses order so latest exprs are on top

        canvas.selection match {
          case Some(selection) =>
            val selectedExpr = selection.expr
            val selectedBlob = selectedExpr.blob

            def updateDragPos(event: ReactMouseEventFromHtml)
              : CallbackTo[(Selection, ReactMouseEventFromHtml)] = {
              val newSelection =
                selection.moveTo(Vector2(event.clientX, event.clientY))
              val newCanvas = canvas.copy(selection = Some(newSelection))
              onCanvasChange(newCanvas) >> CallbackTo.pure(
                (newSelection, event))
            }

            def releaseDrag(
                newSelectionAndEvent: (Selection, ReactMouseEventFromHtml))
              : Callback = {
              val newSelection = newSelectionAndEvent._1
              val event = newSelectionAndEvent._2

              //TODO Find any holes the elem is in, and make it enter the hole.
              //val newSelectedExpr = newSelection.expr
              val selfDOM = event.currentTarget
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

            //Last-minute
            val selfDOM = global.document.getElementById("canvas").asInstanceOf[html.Element]
            val myBounds = Rect(selfDOM.getBoundingClientRect())
            val mousePos = selection.mousePos
            val isInBody = mousePos.isInRect(myBounds)

            val selectedExprRendered = PhysBlobComponent(
              selectedExpr,
              disablePointers = isInBody,
              onPhysBlobChange = { newSelectedExpr =>
                val newSelection = selection.copy(expr = newSelectedExpr)
                val newCanvas = canvas.copy(selection = Some(newSelection))
                onCanvasChange(newCanvas)
              },
              onPhysBlobDragStart = { (event, selectedBlob) =>
                Callback {
                  global.console.warn(
                    s"Selected block 'selected' via dragge again: $event | $selectedBlob")
                }
              },
              //Selected elements can't fill - they would fill themselves.
              onFillOrEmpty = { _ =>
                Callback.empty
              }
            )

            <.div(
              paperstx.Styles.canvas,
              ^.id := "canvas",
              ^.onMouseMove ==> { event: ReactMouseEventFromHtml =>
                updateDragPos(event) >> Callback.empty
              },
              ^.onMouseUp ==> { event: ReactMouseEventFromHtml =>
                updateDragPos(event) >>= releaseDrag
              },
              //Needs to be before selected expression rendered so selected is on top.
              otherExprsRendered(onFillOrEmpty = { (replaceBlobWith, holeOp) => holeOp match {
                case EmptyHole(_, _, _) => Callback.empty //Can't empty with selection
                case FillHole(fill) =>
                  fill(selectedBlob) match {
                    case None => Callback.empty //Couldn't fill.
                    case Some(filledBlob) =>
                      val newCanvas = replaceBlobWith(filledBlob) //Adds inner blob to outer blob
                        .withoutSelection //Removes (extra) inner blob
                      onCanvasChange(newCanvas)
                  }
              } }),
              selectedExprRendered
            )
          case None =>
            <.div(paperstx.Styles.canvas,
              ^.id := "canvas",
              otherExprsRendered(
              onFillOrEmpty = { (replaceBlobWith, holeOp) => holeOp match {
                case FillHole(_) => Callback.empty //Can't fill without selection
                case EmptyHole(emptyCon, emptyEvent, goneBlob) =>
                  val roughGoneDOM = emptyEvent.currentTarget
                  val roughGoneBounds = Rect(roughGoneDOM.getBoundingClientRect())
                  val gonePhysBlob = PhysBlob(roughGoneBounds, goneBlob)
                  val selectionPos = Vector2(emptyEvent.clientX, emptyEvent.clientY)
                  val newSelection = Selection(gonePhysBlob, selectionPos)

                  val newCanvas = replaceBlobWith(emptyCon) //Removes inner from outer
                   .addSelectExpr(newSelection) //Adds inner standalone
                  onCanvasChange(newCanvas)
            } }))
        }
      }
      .build

  def apply(canvas: Canvas, onCanvasChange: Canvas => Callback): VdomElement =
    component(Props(canvas, onCanvasChange))
}
