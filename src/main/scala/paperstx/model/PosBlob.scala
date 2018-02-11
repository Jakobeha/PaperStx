package paperstx.model

import org.scalajs.dom.raw.Position

/**
  * A [[paperstx.model.Blob]] with a position (x and y coordinates).
  */
case class PosBlob(pos: Position, blob: Blob.Full)
