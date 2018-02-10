package paperstx.util

/**
  * Lets you create recursive types.
  *
  * @example `type B = A[B]` doesn't compile. But `type B = A[UnFix[B]]` does,
  *         and provides almost the same functionality.
  */
case class UnFix[T[_]](content: Fix[T])

object UnFix {
  implicit def apply[T[_]](content: Fix[T]): UnFix[T] = new UnFix[T](content)
  implicit def unapply[T[_]](arg: UnFix[T]): Option[Fix[T]] = Some(arg.content)
  implicit def unapplySome[T[_]](arg: UnFix[T]): Fix[T] = arg.content
}
