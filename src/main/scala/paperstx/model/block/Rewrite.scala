package paperstx.model.block

import paperstx.util.TraverseFix.TraversableMap
import paperstx.util.ColExts.MapExt

import scalaz.Applicative
import scalaz.Scalaz._

/** Changes dependent types before they're resolved. */
case class Rewrite[T](rewritten: Map[DependentType, FunctionType[T]]) {

  /** Applies rewrites from both - right rewrites (probably?) override. */
  def ++(other: Rewrite[T]): Rewrite[T] =
    Rewrite(this.rewritten ++ other.rewritten)

  /** @note Discards function's old inputs, because they won't affect the type itself, only sub-types. */
  def rewrite(
      functionType: FunctionType[DependentType]): Option[FunctionType[T]] =
    rewrite(functionType.typ)

  def rewrite(typ: DependentType): Option[FunctionType[T]] = rewritten.get(typ)

  /** Transforms the types which replace, not the types which are replaced. */
  def mapOut[T2](f: T => T2): Rewrite[T2] =
    Rewrite(rewritten.mapValues(_.map(f)))

  /** Transforms the types which replace, not the types which are replaced.
    * Appends function inputs (probably replaces on overlap). */
  def mapUncurryOut[T2](f: T => FunctionType[T2]): Rewrite[T2] =
    Rewrite(rewritten.mapValues(_.mapUncurry(f)))

  /** Transforms the types which replace, not the types which are replaced. */
  def traverseOut[T2, F[_]: Applicative](f: T => F[T2]): F[Rewrite[T2]] =
    rewritten.traverseValuesF(_.traverse(f)).map(Rewrite.apply)

  /** A summary of rewritten values without an enclosure, typically for printing. */
  lazy val openSummary: String = rewritten
    .map {
      case (key, value) => s"$key = $value"
    }
    .mkString(sep = ", ")

  override def toString = {
    s"Rewrite($openSummary)"
  }
}

object Rewrite {

  implicit class DependentRewrite(self: Rewrite[DependentType]) {

    /** Returns original type if can't rewrite. */
    def partialRewrite(functionType: FunctionType[DependentType])
      : FunctionType[DependentType] =
      self.rewritten.getOrElse(functionType.typ, functionType)

    /** Returns original type if can't rewrite. */
    def partialRewrite(typ: DependentType): FunctionType[DependentType] =
      self.rewritten.getOrElse(typ, FunctionType.pure(typ))

    /** Resolves the rewritten outputs in the scope,
      * and creates a scope mapping the rewritten inputs to them. */
    def subScope(superScope: Scope): Scope =
      Scope.simple(self.rewritten.mapMaybeValues(superScope.resolve)) // TODO Should types be fully resolved?
  }

  /** Rewrites nothing. */
  def empty[T]: Rewrite[T] = Rewrite(rewritten = Map.empty)

  /** Rewrites the given types to themselves - semantically equivalent to [[paperstx.model.block.Rewrite.empty]] */
  def id(types: Traversable[DependentType]): Rewrite[DependentType] =
    Rewrite(
      types.toSet
        .map((typ: DependentType) => (typ, FunctionType.pure(typ)))
        .toMap)
}
