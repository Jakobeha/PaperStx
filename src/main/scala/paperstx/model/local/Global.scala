package paperstx.model.local

import paperstx.model.block.BlockClass

import scalaz.Writer
import scalaz.Scalaz._

object Global {
  def apply[T](extraClasses: Traversable[BlockClass], x: T): Global[T] =
    Writer(extraClasses.toList, x)

  def pure[T](x: T): Global[T] = x.point[Global]
}
