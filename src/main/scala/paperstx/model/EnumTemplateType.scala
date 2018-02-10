package paperstx.model

import paperstx.util.HueColor

import scalaz.{Equal, Functor}
import scalaz.Scalaz._

case class EnumTemplateType[TColor](label: String, color: TColor) {
  override def equals(other: scala.Any): Boolean = other match {
    case enumTemplateType: EnumTemplateType[_] =>
      this.label === enumTemplateType.label
    case _ => false
  }

  override val hashCode: Int = label.hashCode

  def traverseColor[TNewColor, F[_]: Functor](
      f: TColor => F[TNewColor]): F[EnumTemplateType[TNewColor]] = {
    f(color).map { EnumTemplateType(this.label, _) }
  }
}

object EnumTemplateType {
  type Full = EnumTemplateType[HueColor]

  implicit def equal[TColor]: Equal[EnumTemplateType[TColor]] =
    Equal.equal(_ == _)
}
