package paperstx.model.block

import paperstx.util.TraverseFix.TraversableSeq
import scalaz.Applicative
import scalaz.Scalaz._

/** Changes inputs and outputs (when used as a function).
  *
  * @param inputs Doesn't affect resolution, probably isn't semantic (just verification).
  * @param outputs Doesn't affect resolution, probably isn't semantic (just verification).*/
case class RewriteUnionType(label: String,
                            types: Seq[ProFunctionType],
                            inputs: Seq[DependentType],
                            outputs: Seq[DependentType]) {
  val unresolved: DependentType = DependentType(label)
  val functionTypes: Seq[FunctionType[DependentType]] =
    types.map(_.functionType)

  def setUnresolved(newUnresolved: DependentType): RewriteUnionType =
    this.copy(label = newUnresolved.label)

  /** This type as a property of the instance. */
  def asProperty(instanceBind: String): RewriteUnionType =
    this.copy(label = s"$instanceBind>$label",
              // TODO Should inputs/outputs and args also be localized?
              types = types.map(_.overHeadType(_.asProperty(instanceBind))))

  def mapTypes(f: ProFunctionType => ProFunctionType): RewriteUnionType =
    this.copy(types = types.map(f))

  def mapFunctionTypes(
      f: FunctionType[DependentType] => FunctionType[DependentType])
    : RewriteUnionType =
    mapTypes(_.overFunctionType(f))

  def traverseTypes[F[_]: Applicative](
      f: ProFunctionType => F[ProFunctionType]): F[RewriteUnionType] =
    types.traverseF(f).map(newTypes => this.copy(types = newTypes))

  def traverseFunctionTypes[F[_]: Applicative](
      f: FunctionType[DependentType] => F[FunctionType[DependentType]])
    : F[RewriteUnionType] =
    traverseTypes(_.traverseFunctionType(f))

  /** Resolves by resolving pro-function types. */
  private def foldMapResolveTypes[F[_]: Applicative](
      f: ProFunctionType => F[BlockType]): F[BlockType] =
    types.traverseF(f).map(BlockType.union(label, _, inputs, outputs))

  /** Resolves by resolving function types. */
  def foldMapResolveFunctionTypes[F[_]: Applicative](
      f: FunctionType[DependentType] => F[BlockType]): F[BlockType] =
    foldMapResolveTypes(typ => f(typ.functionType))

  override def toString = {
    val inputsSummary = inputs.map(_.toString).mkString(", ")
    val outputsSummary = outputs.map(_.toString).mkString(", ")
    s"$label{ = ${types.mkString(sep = " | ")} }($inputsSummary)[$outputsSummary]?"
  }
}
