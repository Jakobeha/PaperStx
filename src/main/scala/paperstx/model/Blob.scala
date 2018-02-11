package paperstx.model

import scalaz.Applicative
import scalaz.Scalaz._

sealed trait Blob[T] {
  def overTemplate[TNew](f: T => TNew): Blob[TNew]

  def traverseTemplate[TNew, F[_]: Applicative](f: T => F[TNew]): F[Blob[TNew]]
}

case class FreeBlob[T](content: String) extends Blob[T] {
  override def overTemplate[TNew](f: T => TNew) = {
    FreeBlob(content)
  }

  override def traverseTemplate[TNew, F[_]: Applicative](f: T => F[TNew]) = {
    (FreeBlob(content): Blob[TNew]).point[F]
  }
}

case class TemplateBlob[T](content: T) extends Blob[T] {
  override def overTemplate[TNew](f: T => TNew) = {
    TemplateBlob(f(content))
  }

  override def traverseTemplate[TNew, F[_]: Applicative](f: T => F[TNew]) = {
    f(content).map(TemplateBlob.apply)
  }
}

object Blob {
  implicit class FullBlob(self: Blob.Full) {

    /**
      * Whether the blob can fill the hole with the given skeleton.
      * Free blobs can fill any hole, template blobs can fill the same
      * holes as their templates.
      */
    def fitsIn(skeleton: HoleSkeleton): Boolean = self match {
      case FreeBlob(_)            => true
      case TemplateBlob(template) => template.fitsIn(skeleton)
    }
  }

  type Full = Blob[TypedTemplate.Full]

  implicit class TemplateBlob[TPhase <: Phase](self: Blob[Template[TPhase]]) {
    def assignType(typ: EnumTemplateType): Blob[TypedTemplate[TPhase]] = {
      self.overTemplate {
        TypedTemplate(typ, _)
      }
    }
  }
}
