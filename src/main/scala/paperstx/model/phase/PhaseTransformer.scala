package paperstx.model.phase

import paperstx.model.block.Block

trait PhaseTransformer[TIn <: Phase, TOut <: Phase, F[_]] {
  def traverseBlock(block: Block[TIn]): F[Block[TOut]] =
    block.traversePhase(this)

  def traverseBlockType(blockType: TIn#BlockType): F[TOut#BlockType]

  def dependent: PhaseTransformer[TIn#Dependent, TOut#Dependent, F]
}
