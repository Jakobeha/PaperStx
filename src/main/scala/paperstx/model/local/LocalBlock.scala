package paperstx.model.local

import paperstx.model.block._

import paperstx.util.TraverseFix.TraversableSeq

/** A block with local classes. These classes are anonymized and made global. */
case class LocalBlock(block: Block, subClasses: Seq[LocalBlockClass]) {

  /** Puts the sub-classes into the root scope. Also modifies references in the block. */
  def globalize(anonId: Int,
                classId: Int,
                scope: Scope): Global[RewriteBlock] = {
    val transferred = block.instanceOutputs(scope)
    subClasses.zipWithIndex
      .traverseF {
        case (subClass, idx) =>
          subClass.globalizeWithMapping(idx,
                                        anonId,
                                        classId,
                                        scope,
                                        transferred)
      }
      .flatMap { globalsAndMappings =>
        val (globalClasses, globalMappings) = globalsAndMappings.unzip
        val globalRewrite = Rewrite(globalMappings.toMap)
        val globalBlock =
          RewriteBlock(block.rewriteTypes(globalRewrite), globalRewrite)
        Global(globalClasses, globalBlock)
      }
  }
}
