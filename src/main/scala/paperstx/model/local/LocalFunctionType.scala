package paperstx.model.local

import paperstx.model.block._
import paperstx.util.TraverseFix.TraversableSeq

/** Rewrites inputs *and outputs* - outputs are rewritten to local classes. */
case class LocalFunctionType(typ: DependentType,
                             inputs: Rewrite[DependentType],
                             outputSubClasses: Seq[LocalBlockClass]) {
  val functionType: FunctionType[DependentType] = FunctionType(typ, inputs)

  def instanceOutputs(scope: Scope): Seq[DependentType] =
    scope
      .semiResolve(functionType)
      .map(_.outputs)
      .getOrElse(Seq.empty)
      .map(_.asProperty("self"))

  /** Puts the sub-classes into the root scope. Also modifies references in the pro-function. */
  def globalize(anonId: Int,
                classId: Int,
                scope: Scope): Global[ProFunctionType] = {
    val transferred = instanceOutputs(scope)
    outputSubClasses.zipWithIndex
      .traverseF {
        case (outputSubClass, idx) =>
          outputSubClass.globalizeWithMapping(idx,
                                              anonId,
                                              classId,
                                              scope,
                                              transferred)
      }
      .flatMap {
        globalsAndMappings: Seq[
          (BlockClass, (DependentType, FunctionType[DependentType]))] =>
          val (globalClasses, globalMappings) = globalsAndMappings.unzip
          val globalRewrite = Rewrite(globalMappings.toMap)
          val absoluteProFunctionType =
            ProFunctionType(typ,
                            inputs.mapUncurryOut(globalRewrite.partialRewrite),
                            globalRewrite)
          Global(absoluteProFunctionType, globalClasses)
      }
  }
}
