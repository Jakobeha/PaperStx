package paperstx.model.block

import scalaz.Applicative
import scalaz.Scalaz._

/** A block annotated with type information. */
case class TypedBlock(rewriteBlock: RewriteBlock, typ: EnumType) {
  val block: Block = rewriteBlock.block

  def setBlock(newBlock: Block): TypedBlock =
    this.copy(rewriteBlock = rewriteBlock.copy(block = newBlock))

  def overType(f: EnumType => EnumType): TypedBlock = this.copy(typ = f(typ))

  def traverseType[F[_]: Applicative](
      f: EnumType => F[EnumType]): F[TypedBlock] =
    f(typ).map(newType => this.copy(typ = newType))
}
