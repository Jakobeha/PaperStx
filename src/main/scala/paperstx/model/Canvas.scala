package paperstx.model

import scala.scalajs.js.Dynamic.global

/**
  * Like a source file, contains code in block form -
  * specifically, in the form of [[paperstx.model.Blob]]s.
  * Also contains the expressions' physical bounds,
  * such as position and size,
  * and can have a "selected" expression which is
  * outside of the regular list, with a mouse position
  */
case class Canvas(otherExprs: List[PhysBlob], selection: Option[Selection]) {

  /**
    * Every expression in the canvas, including the selected one.
    */
  lazy val allExprs: List[PhysBlob] = selection match {
    case None             => otherExprs
    case Some(selectionn) => selectionn.expr :: otherExprs
  }

  /**
    * Adds an expression to the canvas, doesn't select it.
    */
  def addExpr(expr: PhysBlob): Canvas = {
    this.copy(otherExprs = expr :: this.otherExprs)
  }

  /**
    * Adds an expression to the canvas, selects it.
    * If another expression is selected, deselects it.
    */
  def addSelectExpr(newSelection: Selection): Canvas = this.selection match {
    case None => Canvas(this.otherExprs, Some(newSelection))
    case Some(oldSelection) =>
      Canvas(oldSelection.expr :: this.otherExprs, Some(newSelection))
  }

  /**
    * Selects an expression in `otherExprs`.
    * Constraint (warns otherwise): The expression is in `otherExprs`.
    */
  def select(newSelection: Selection): Canvas = {
    val restOtherExprs = otherExprs diff List(newSelection.expr)
    if (otherExprs == restOtherExprs) {
      global.console.warn(
        s"Canvas selected expression '${newSelection.expr}' which wasn't in the list of expressions.")
    }

    val newOtherExprs = this.selection match {
      case None             => restOtherExprs
      case Some(selectionn) => selectionn.expr :: restOtherExprs
    }

    Canvas(newOtherExprs, Some(newSelection))
  }

  /**
    * Deselects the selected expression, and puts it in front of `otherExprs`.
    * Constraint (warns otherwise): An expression is selected.
    */
  def deselect: Canvas = {
    this.selection match {
      case None =>
        global.console.warn(
          s"Canvas tried to deselect when no expression was selected.")
        this
      case Some(selectionn) =>
        Canvas(selectionn.expr :: otherExprs, selection = None)
    }
  }

  /**
    * Deselects the selected expression, and removes it from the list.
    * Constraint (warns otherwise): An expression is selected.
    */
  def withoutSelection: Canvas = {
    this.selection match {
      case None =>
        global.console.warn(
          s"Canvas tried to remove selected when no expression was selected.")
        this
      case Some(_) =>
        Canvas(otherExprs, selection = None)
    }
  }
}

object Canvas {
  def empty: Canvas = Canvas(otherExprs = List.empty, selection = None)
}
