package paperstx.model.block

import paperstx.util.ColExts.MapExt
import scalaz.Scalaz._

/** Resolves types. */
case class Scope(resolvedTypes: Map[DependentType, ResolvingType]) {

  /** Has resolved types from both scopes - right scope prioritized (really?). */
  def ++(other: Scope): Scope = Scope(this.resolvedTypes ++ other.resolvedTypes)

  /** Transforms the dependent types which resolve. */
  def mapInputs(f: DependentType => DependentType): Scope =
    Scope(resolvedTypes.mapKeys(f))

  /** @note Discards the function's input unless it rewrites the type itself,
    * because the output doesn't contain sub-types. */
  def resolve(functionType: FunctionType[DependentType]): Option[BlockType] =
    semiResolve(functionType).flatMap(furtherResolve)

  def resolve(typ: DependentType): Option[BlockType] =
    semiResolve(typ).flatMap(furtherResolve)

  /** @note Discards the function's input unless it rewrites the type itself,
    * because the output doesn't contain sub-types. */
  def semiResolve(
      functionType: FunctionType[DependentType]): Option[ResolvingType] =
    semiResolve(functionType.typ).map {
      case ResolvedType(blockType) => ResolvedType(blockType)
      case UnresolvedType(rewriteUnionType) =>
        UnresolvedType(
          rewriteUnionType.mapFunctionTypes(functionType.inputs.partialRewrite))
    }

  def semiResolve(typ: DependentType): Option[ResolvingType] =
    resolvedTypes.get(typ)

  /** Will resolve the type if it's still unresolved, otherwise will return it as-is. */
  def furtherResolve(resolvingType: ResolvingType): Option[BlockType] =
    resolvingType match {
      case ResolvedType(blockType)          => Some(blockType)
      case UnresolvedType(rewriteUnionType) => furtherResolve(rewriteUnionType)
    }

  private def furtherResolve(
      rewriteUnionType: RewriteUnionType): Option[BlockType] =
    rewriteUnionType.traverseResolveFunctionTypes(resolve)

  /** Takes the rewritten map, resolves the outputs in this scope, and makes that the new scope. */
  def rewrite(rewrite: Rewrite[DependentType], rootScope: Scope): Scope =
    Scope.simple(rewrite.rewritten.mapMaybeValues((rootScope ++ this).resolve))

  /** A summary of resolved values without an enclosure, typically for printing. */
  lazy val openSummary: String = resolvedTypes
    .map {
      case (key, value) => s"$key => $value"
    }
    .mkString(sep = ", ")

  override def toString = {
    s"Scope($openSummary)"
  }
}

object Scope {

  /** Doesn't resolve any types. */
  val empty: Scope = Scope(Map.empty[DependentType, ResolvingType])

  /** All types are immediately resolved - no mapping types to partially-resolved other types.
    * @note Would be `apply`, but the compiler won't accept because the signature is the same after erasure. */
  def simple(resolvedTypes: Map[DependentType, BlockType]): Scope =
    Scope(resolvedTypes.mapValues(ResolvedType.apply))

  /** Resolves the given types' dependent counterparts. */
  def absolute(types: Traversable[ResolvingType]): Scope =
    Scope(types.map(typ => (typ.unresolved, typ)).toMap)

  /** Has resolved types from all scopes - later scopes prioritized (really?). */
  def concat(scopes: TraversableOnce[Scope]): Scope =
    scopes.reduceOption(_ ++ _).getOrElse(Scope.empty)
}
