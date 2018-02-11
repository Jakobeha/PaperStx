package paperstx.model

/**
  * Like a source file, contains code in block form -
  * specifically, in the form of [[paperstx.model.Blob]]s.
  * Also contains the expressions positions,
  * which are used when the canvas is rendered
  * to render the expressions.
  */
case class Canvas(exprs: Seq[PosBlob])

object Canvas {
  def empty: Canvas = Canvas(exprs = Seq.empty)
}
