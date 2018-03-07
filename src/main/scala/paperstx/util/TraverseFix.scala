package paperstx.util

import scalaz.Applicative
import scalaz.Scalaz._

/**
  * Adds `Traversable` instances to built-in classes.
  */
object TraverseFix {
  implicit class TraversableMap[K, I](private val self: Map[K, I]) {
    def traverseValuesF[F[_]: Applicative, O](
        transformer: I => F[O]): F[Map[K, O]] =
      self.toList
        .traverse { case (key, value) => transformer(value).map((key, _)) }
        .map { _.toMap }
  }

  implicit class TraversableSet[I](private val self: Set[I]) {
    def traverseF[F[_]: Applicative, O](transformer: I => F[O]): F[Set[O]] =
      self.toList.traverse(transformer).map { _.toSet }
  }

  implicit class TraversableSeq[I](private val self: Seq[I]) {
    def traverseF[F[_]: Applicative, O](transformer: I => F[O]): F[Seq[O]] =
      self.toList.traverse(transformer).map { _.toSeq }
  }
}
