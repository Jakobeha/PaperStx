package paperstx.model.block

import paperstx.util.TraverseFix.TraversableMap

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
  def mapOut[T2](f: T => T2): Rewrite[T2] = mapFuncOut(_.map(f))

  /** Transforms the types which replace, not the types which are replaced.
    * Appends function inputs (probably replaces on overlap). */
  def mapUncurryOut[T2](f: T => FunctionType[T2]): Rewrite[T2] =
    mapFuncOut(_.mapUncurry(f))

  /** Transforms the types which replace, not the types which are replaced. */
  def traverseOut[T2, F[_]: Applicative](f: T => F[T2]): F[Rewrite[T2]] =
    traverseFuncOut(_.traverse(f))

  /** Transforms the function types which replace, not the types which are replaced. */
  private def mapFuncOut[T2](
      f: FunctionType[T] => FunctionType[T2]): Rewrite[T2] =
    Rewrite(rewritten.mapValues(f))

  /** Transforms the function types which replace, not the types which are replaced. */
  private def traverseFuncOut[T2, F[_]: Applicative](
      f: FunctionType[T] => F[FunctionType[T2]]): F[Rewrite[T2]] =
    rewritten.traverseValuesF(f).map(Rewrite.apply)

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

    /** Returns original type with input outputs rewritten if can't rewrite. */
    def partialRewrite(functionType: FunctionType[DependentType])
      : FunctionType[DependentType] =
      self.rewritten.getOrElse(functionType.typ, functionType)

    /** Returns original type if can't rewrite. */
    def partialRewrite(typ: DependentType): FunctionType[DependentType] =
      self.rewritten.getOrElse(typ, FunctionType.pure(typ))

    /** Partially rewrites the output types using the other rewrite.
      * If it succeeds, gets rid of the output types' inputs.
      * If it fails keeps the inputs, and the inputs themselves might also get rewritten.
      *
      * @example ```
      *          Rewrite(A2 => A1(B1 = B2)).partialRewriteOut(
      *          Rewrite(A3 => A2(C1 = C2), A3 => D(E = A2))) ==>
      *          Rewrite(A3 => A1(B1 = B2), A3 => D(E = A1(B1 = B2)))
      *          ```
      *          (`(C1 = C2)` (I believe should?) get discarded). */
    def partialRewriteOut(
        outRewrite: Rewrite[DependentType]): Rewrite[DependentType] =
      self.mapFuncOut(outRewrite.partialRewrite)

    /** Resolves the rewritten outputs in the scope,
      * and creates a scope mapping the rewritten inputs to them. */
    def subScope(superScope: Scope): Scope =
      Scope.simple(
        self.rewritten
          .mapValues(superScope.resolve)
          .mapValues(_.eval)) // TODO Should types be fully resolved?
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
