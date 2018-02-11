package paperstx.components

import japgolly.scalajs.react.{
  Callback,
  CallbackTo,
  ReactMouseEventFromHtml,
  ScalaComponent
}
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model.{Canvas, Selection}
import paperstx.util.{Rect, Vector2}

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

        val otherExprsRendered = otherExprs.zipWithIndex
          .map {
            case (physBlob, idx) =>
              PhysBlobComponent(
                physBlob,
                onPhysBlobChange = { newPhysBlob =>
                  val newExprs = otherExprs.updated(idx, newPhysBlob)
                  val newCanvas = canvas.copy(otherExprs = newExprs)
                  onCanvasChange(newCanvas)
                },
                onPhysBlobDragStart = { (event, physBlob) =>
                  val selection =
                    Selection(physBlob, Vector2(event.clientX, event.clientY))
                  val newCanvas = canvas.select(selection)
                  onCanvasChange(newCanvas)
                }
              )
          }
          .reverse
          .toTagMod //Reverses order so latest exprs are on top

        canvas.selection match {
          case Some(selection) =>
            val selectedExpr = selection.expr

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
              println(s"$mousePos + $myBounds")
              val newCanvas =
                if (mousePos.isInRect(myBounds)) {
                  semiNewCanvas.deselect
                } else { //Dragged out
                  semiNewCanvas.withoutSelection
                }
              onCanvasChange(newCanvas)
            }

            val selectedExprRendered = PhysBlobComponent(
              selectedExpr,
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
              }
            )

            <.div(
              paperstx.Styles.canvas,
              ^.onMouseMove ==> { event: ReactMouseEventFromHtml =>
                updateDragPos(event) >> Callback.empty
              },
              ^.onMouseUp ==> { event: ReactMouseEventFromHtml =>
                updateDragPos(event) >>= releaseDrag
              },
              otherExprsRendered, //Needs to be before selected expression rendered so selected is on top.
              selectedExprRendered
            )
          case None =>
            <.div(paperstx.Styles.canvas, otherExprsRendered)
        }
      }
      .build

  def apply(canvas: Canvas, onCanvasChange: Canvas => Callback): VdomElement =
    component(Props(canvas, onCanvasChange))
}
