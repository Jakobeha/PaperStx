package paperstx.model

import scalaz.Applicative
import scalaz.Scalaz._

sealed trait Blob[T] {
  def traverseTemplate[TNew, F[_]: Applicative](f: T => F[TNew]): F[Blob[TNew]]
}

case class StringBlob[T](content: String) extends Blob[T] {
  override def traverseTemplate[TNew, F[_]: Applicative](f: T => F[TNew]) = {
    (StringBlob(content): Blob[TNew]).point[F]
  }
}

case class TemplateBlob[T](content: T) extends Blob[T] {
  override def traverseTemplate[TNew, F[_]: Applicative](f: T => F[TNew]) = {
    f(content).map(TemplateBlob.apply)
  }
}
