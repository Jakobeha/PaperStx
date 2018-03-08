package paperstx.model

import scalaz.Scalaz.{Id, listMonoid}
import scalaz.{Applicative, Monoid, Writer, WriterT}

package object block {

  /** A value which was derived from dependent types. Notes which derived types failed to resolve. */
  type Resolve[T] = Writer[List[DependentType], T]

  implicit class ResolveExt[T](private val self: Resolve[T]) {
    val eval: T = self.run._2
  }

  implicit val resolveApplicative: Applicative[Resolve] =
    WriterT.writerTApplicative[Id, List[DependentType]]

  //Prevents users of `Global` from importing scalaz.Scalaz._
  implicit def resolveListMonoid: Monoid[List[DependentType]] = listMonoid
}
