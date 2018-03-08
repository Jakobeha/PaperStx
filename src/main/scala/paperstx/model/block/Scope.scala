package paperstx.model.block

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
  def resolve(functionType: FunctionType[DependentType]): Resolve[BlockType] =
    semiResolve(functionType).flatMap(furtherResolve)

  /** @note Discards the function's input unless it rewrites the type itself,
    * because the output doesn't contain sub-types. */
  def semiResolve(
      functionType: FunctionType[DependentType]): Resolve[ResolvingType] =
    semiResolve(functionType.typ).map {
      case ResolvedType(blockType) => ResolvedType(blockType)
      case UnresolvedType(rewriteUnionType) =>
        UnresolvedType(
          rewriteUnionType.mapFunctionTypes(functionType.inputs.partialRewrite))
    }

  def semiResolve(typ: DependentType): Resolve[ResolvingType] =
    resolvedTypes.get(typ) match {
      case None                => Resolve.fail(typ)
      case Some(resolvingType) => Resolve.success(resolvingType)
    }

  /** Rewrites inputs for the unresolved type into inputs for the resolved type. */
  def transferRewritesByType(functionType: FunctionType[DependentType])
    : Map[EnumType, Rewrite[DependentType]] =
    semiResolve(functionType).eval match {
      case ResolvedType(_) => Map.empty
      case UnresolvedType(rewriteUnionType) =>
        transferRewritesByTypeFrom(rewriteUnionType)
    }

  /** Rewrites inputs for the resolving type into inputs for the resolved type. */
  private def transferRewritesByTypeFrom(rewriteUnionType: RewriteUnionType)
    : Map[EnumType, Rewrite[DependentType]] =
    rewriteUnionType.functionTypes
      .map { functionType =>
        val immRewrite = functionType.inputs //e.g. A2 => A1(B1 = B2)
        semiResolve(functionType).eval match {
          case ResolvedType(blockType) =>
            blockType.subTypes.map((_, immRewrite)).toMap
          case UnresolvedType(nextType) =>
            transferRewritesByTypeFrom(nextType)
            /* e.g. A3 => A2(C1 = C2), and A3 => D(E = A2)
               becomes A3 => A1(B1 = B2), and A3 => D(E = A1(B1 = B2))
               ((C1 = C2) (I believe should?) get discarded) */
              .mapValues(_.partialRewriteOut(immRewrite))
        }
      }
      /* No ideal way to combine the value rewrites - technically keys shouldn't overlap.
         The values would combine in e.g.
         ```
         A
         < D
         < E
         | B(C = D, F = G)
         | B(C = E, H = I)
         ```
         If a block `B` fills a hole of this type, should its `C` be `D` or `E`, and should it have `F` and/or `H`?
         This is ambiguous. */
      .reduceOption(_ ++ _)
      .getOrElse(Map.empty)

  /** Will resolve the type if it's still unresolved, otherwise will return it as-is. */
  def furtherResolve(resolvingType: ResolvingType): Resolve[BlockType] =
    resolvingType match {
      case ResolvedType(blockType)          => Resolve.pure(blockType)
      case UnresolvedType(rewriteUnionType) => furtherResolve(rewriteUnionType)
    }

  private def furtherResolve(
      rewriteUnionType: RewriteUnionType): Resolve[BlockType] =
    rewriteUnionType.foldMapResolveFunctionTypes(resolve)

  /** Takes the rewritten map, resolves the outputs in this scope, and makes that the new scope. */
  def rewrite(rewrite: Rewrite[DependentType], rootScope: Scope): Scope =
    Scope.simple(
      rewrite.rewritten
        .mapValues((rootScope ++ this).resolve)
        .mapValues(_.eval))

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
