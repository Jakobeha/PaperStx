package paperstx.model.block

import scalaz.Applicative
import scalaz.Scalaz._

/** A (usually dependent) type with inputs. */
case class FunctionType[T](typ: T, inputs: Rewrite[T]) {

  /** Transforms type and inputs. */
  def map[T2](f: T => T2): FunctionType[T2] =
    FunctionType(f(typ), inputs.mapOut(f))

  /** Transforms type and inputs. Appends new inputs (probably replaces on overlap). */
  def mapUncurry[T2](f: T => FunctionType[T2]): FunctionType[T2] =
    FunctionType.uncurry(f(typ), inputs.mapUncurryOut(f))

  /** Transforms type and inputs. */
  def traverse[T2, F[_]: Applicative](f: T => F[T2]): F[FunctionType[T2]] =
    (f(typ) |@| inputs.traverseOut(f))(FunctionType.apply)

  override def toString = s"$typ(${inputs.openSummary})"
}

object FunctionType {

  /** Creates a function with the base type and inputs,
    * and additonal inputs - base type inputs (probably?) override. */
  def uncurry[T](base: FunctionType[T], inputs: Rewrite[T]): FunctionType[T] =
    FunctionType(base.typ, inputs ++ base.inputs)

  /** No params. */
  def pure[T](typ: T): FunctionType[T] =
    FunctionType(typ, inputs = Rewrite.empty)
}
