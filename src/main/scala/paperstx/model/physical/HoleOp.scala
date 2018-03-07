package paperstx.model.physical

import japgolly.scalajs.react.ReactMouseEventFromHtml
import paperstx.model.block.Blob

/**
  * An operation which can be done on a hole within the container.
  * An empty hole can be filled, and a filled hole can be emptied.
  * Both of these cases are represented by subclasses of [[HoleOp]], and [[HoleOp]]
  * provides all of the input requirements and extra output information
  * for these operations.
  */
sealed trait HoleOp[TCon] {
  def overContainer[TCon2](f: TCon => TCon2): HoleOp[TCon2]
}

/**
  * Allows you to fill some hole in the given container,
  * provided that the content blob satisfies some condition
  * (e.g. it fits in the skeleton).
  */
case class FillHole[TCon](fill: Blob => Option[TCon]) extends HoleOp[TCon] {
  override def overContainer[TCon2](f: TCon => TCon2) =
    FillHole(fill.andThen { _.map(f) })
}

/**
  * Allows you to empty some hole in the given container,
  * and also provides the removed item, and event information
  * for rendering this item and allowing user interaction.
  */
case class EmptyHole[TItm, TCon](emptiedCon: TCon,
                                 emptyEvent: ReactMouseEventFromHtml,
                                 goneBlob: Blob)
    extends HoleOp[TCon] {
  override def overContainer[TCon2](f: TCon => TCon2) =
    EmptyHole(f(emptiedCon), emptyEvent, goneBlob)
}

object HoleOp {

  /**
    * Converts a function taking an operation for filling a hole in a container,
    * and a function to re-insert a smaller container into the larger container.
    * Returns a function taking an operation for filling a hole in the smaller container.
    */
  def narrow[TL, TS, R](broader: HoleOp[TL] => R, rewrap: TS => TL)(
      holeOp: HoleOp[TS]): R = {
    broader(holeOp.overContainer(rewrap))
  }
}
