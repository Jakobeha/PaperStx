package paperstx.model.block

import scalaz.Scalaz._
import scalaz.Writer

object Resolve {

  def apply[T](failures: Traversable[DependentType], x: T): Resolve[T] =
    Writer(failures.toList, x)

  /** A value which wasn't derived from any dependent types. Equivalent to `success`. */
  def pure[T](x: T): Resolve[T] = x.point[Resolve]

  /** A value which was derived from dependent types which all resolved. Descriptive version of `pure`. */
  def success(x: ResolvingType): Resolve[ResolvingType] = Resolve.pure(x)

  /** An empty resolving type, notes the dependent type failed to resolve.
    * The resolving type derived from the failed dependent type. */
  def fail(x: DependentType): Resolve[ResolvingType] =
    Resolve(List(x), ResolvingType.empty(x.label))
}
