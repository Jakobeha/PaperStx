package paperstx.model

import paperstx.model.block.BlockClass

import scalaz.Id.Id
import scalaz.{Applicative, Monoid, Writer, WriterT}
import scalaz.Scalaz._

package object local {
  type Global[T] = Writer[List[BlockClass], T]

  object Global {
    def apply[T](x: T, extraClasses: Traversable[BlockClass]): Global[T] =
      Writer(extraClasses.toList, x)

    def pure[T](x: T): Global[T] = x.point[Global]
  }

  implicit val globalApplicative: Applicative[Global] =
    WriterT.writerTApplicative[Id, List[BlockClass]]

  //Prevents users of `Global` from importing scalaz.Scalaz._
  implicit def blockClassListMonoid: Monoid[List[BlockClass]] = listMonoid
}
