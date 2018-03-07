package paperstx.model.block

/** A block whose outputs are rewritten. */
case class RewriteBlock(block: Block, outputs: Rewrite[DependentType]) {

  /** Rewrites the block holes' types and outputs' outputs. */
  def rewriteTypes(rewrite: Rewrite[DependentType]): RewriteBlock =
    RewriteBlock(block.rewriteTypes(rewrite),
                 outputs.mapUncurryOut(rewrite.partialRewrite))

  /** Resolves local outputs. Instance types aren't in the result scope, but they're resolved internally. */
  def localScope(parentScope: Scope): Scope =
    block.justFragScope(parentScope).rewrite(outputs, parentScope)

  /** Contains local outputs and internal instance types. */
  def justFullFragScope(rootScope: Scope): Scope =
    block.justFullFragScope(rootScope) ++ localScope(rootScope)

  /** All the blocks satisfying dependent types within the block. */
  def bindBlocks(scope: Scope): Seq[TypedBlock] = block.bindBlocks(scope)
}
