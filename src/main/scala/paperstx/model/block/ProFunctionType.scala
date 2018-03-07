package paperstx.model.block

import scalaz.Applicative
import scalaz.Scalaz._

/** Rewrites inputs *and outputs*. */
case class ProFunctionType(typ: DependentType,
                           inputs: Rewrite[DependentType],
                           outputs: Rewrite[DependentType]) {
  val functionType: FunctionType[DependentType] = FunctionType(typ, inputs)

  def overHeadType(f: DependentType => DependentType): ProFunctionType =
    this.copy(typ = f(typ))

  def overFunctionType(
      f: FunctionType[DependentType] => FunctionType[DependentType])
    : ProFunctionType = {
    val newFunctionType = f(functionType)
    ProFunctionType(newFunctionType.typ, newFunctionType.inputs, outputs)
  }

  def traverseFunctionType[F[_]: Applicative](
      f: FunctionType[DependentType] => F[FunctionType[DependentType]])
    : F[ProFunctionType] = f(functionType).map { newFunctionType =>
    ProFunctionType(newFunctionType.typ, newFunctionType.inputs, outputs)
  }

  override def toString = s"$typ(${inputs.openSummary})[${outputs.openSummary}]"
}
