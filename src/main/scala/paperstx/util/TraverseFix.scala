package paperstx.util

import scalaz.Applicative
import scalaz.Scalaz._
import scalaz._

/**
  * For some reason `_.traverse` isn't being resolved.
  */
object TraverseFix {
  implicit class TraversableSet[I](private val self: Set[I]) {
    def traverseF[F[_]: Applicative, O](transformer: I => F[O]): F[Set[O]] =
      self.toList.traverse(transformer).map { _.toSet }
  }

  implicit class TraversableSeq[I](private val self: Seq[I]) {
    def traverseF[F[_]: Applicative, O](transformer: I => F[O]): F[Seq[O]] =
      self.toList.traverse(transformer).map { _.toSeq }
  }
}
