package paperstx.model

import scalacss.internal.ValueT.Color

sealed trait TemplateFrag[TType, TTemp, TColor] {

}

case class StaticFrag[TType, TTemp, TColor](text: String) extends TemplateFrag[TType, TTemp, TColor]

case class Hole[TType, TTemp, TColor](typ: TType, content: Option[Blob[TTemp]]) extends TemplateFrag[TType, TTemp, TColor]

object TemplateFrag {
  type Full = TemplateFrag[TemplateType.Full, Template.Full, Color]
}

object StaticFrag {
  type Full = StaticFrag[TemplateType.Full, Template.Full, Color]
}

object Hole {
  type Full = Hole[TemplateType.Full, Template.Full, Color]

  /**
    * A hole with no elements.
    */
  def empty[TType, TTemp, TColor](typ: TType): Hole[TType, TTemp, TColor] = Hole(typ, content = None)
}