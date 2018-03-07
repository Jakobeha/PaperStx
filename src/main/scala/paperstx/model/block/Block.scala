package paperstx.model.block

/** An untyped block - a node in the syntax AST. */
case class Block(frags: Seq[BlockFrag]) {
  lazy val fragsByInstances: Map[String, Seq[BlockFrag]] =
    frags.groupBy(_.instanceBind).collect {
      case (Some(key), value) => (key, value)
    }

  def fragForInstance(instance: String): Option[BlockFrag] =
    frags.find(_.instanceBind.contains(instance))

  /** The scope for fragments of this block - contains all fragments. */
  def fragScope(parentScope: Scope): Scope =
    fragScopeExcluding(parentScope, excludedInstances = Set.empty)

  /** The scope for fragments of this block - contains all fragments. */
  def justFragScope(parentScope: Scope): Scope =
    justFragScopeExcluding(parentScope, excludedInstances = Set.empty)

  /** The scope for fragments of this block - contains other fragments. */
  def justIndivFragScope(instance: Option[String], parentScope: Scope): Scope =
    justFragScopeExcluding(parentScope, excludedInstances = instance.toSet)

  /** The scope for fragments of this block - contains other fragments. */
  def indivFragScope(instance: Option[String], parentScope: Scope): Scope =
    fragScopeExcluding(parentScope, excludedInstances = instance.toSet)

  /** The scope for fragments of this block - contains other fragments. */
  private def indivFragScope(instance: String, parentScope: Scope): Scope =
    fragScopeExcluding(parentScope, excludedInstances = Set(instance))

  /** The scope for fragments of this block - contains not excluded fragments. */
  private def fragScopeExcluding(parentScope: Scope,
                                 excludedInstances: Set[String]): Scope =
    parentScope ++ justFragScopeExcluding(parentScope, excludedInstances)

  /** The scope for fragments of this block - contains not excluded fragments. */
  private def justFragScopeExcluding(parentScope: Scope,
                                     excludedInstances: Set[String]): Scope = {
    def indivFragScopeExcluding(instanceBind: String) = {
      if (excludedInstances.contains(instanceBind)) {
        parentScope
      } else {
        parentScope ++ justFragScopeExcluding(parentScope,
                                              excludedInstances + instanceBind)
      }
    }

    Scope.concat(frags.map(_.instanceScope(indivFragScopeExcluding)))
  }

  /** The scope for fragments of this block, and their blocks' sub-fragments. */
  def justFullFragScope(rootScope: Scope): Scope = {
    justFragScope(rootScope) ++ Scope.concat(frags.flatMap { frag =>
      frag.subBlockScope(indivFragScope(frag.instanceBind, rootScope))
    })
  }

  /** All the blocks satisfying dependent types within the block. */
  def bindBlocks(scope: Scope): Seq[TypedBlock] =
    frags.flatMap(_.bindBlocks(scope))

  def instanceOutputs(scope: Scope): Seq[DependentType] =
    frags.flatMap(_.instanceOutputs(indivFragScope(_, scope)))

  /** Rewrites the block holes' types. */
  def rewriteTypes(rewrite: Rewrite[DependentType]): Block =
    Block(frags.map(_.overType(_.mapUncurry(rewrite.partialRewrite))))
}
