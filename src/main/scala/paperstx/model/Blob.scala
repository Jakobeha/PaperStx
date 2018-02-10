package paperstx.model

sealed trait Blob[T] {

}

case class StringBlob[T](content: String) extends Blob[T]

case class TemplateBlob[T](content: T) extends Blob[T]