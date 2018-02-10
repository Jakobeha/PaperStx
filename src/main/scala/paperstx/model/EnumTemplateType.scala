package paperstx.model

import scalacss.internal.ValueT.Color
import scalaz.Equal
import scalaz.Scalaz._

case class EnumTemplateType[TColor](label: String,
                                    color: TColor) {
  override def equals(other: scala.Any) = other match {
    case enumTemplateType: EnumTemplateType[_] => this.label === enumTemplateType.label
    case _ => false
  }
}

object EnumTemplateType {
  type Full = EnumTemplateType[Color]

  implicit def equal[TColor]: Equal[EnumTemplateType[TColor]] = Equal.equal(_ == _)
}