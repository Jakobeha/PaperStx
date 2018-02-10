package paperstx.model

import scala.scalajs.js.RegExp
import scalacss.internal.ValueT.Color

sealed trait TemplateFrag[TType, TTemp, TColor] {}

case class StaticFrag[TType, TTemp, TColor](text: String)
    extends TemplateFrag[TType, TTemp, TColor]

case class FreeTextFrag[TType, TTemp, TColor](constrainer: RegExp, text: String)
    extends TemplateFrag[TType, TTemp, TColor]

case class Hole[TType, TTemp, TColor](typ: TType,
                                      isBinding: Boolean,
                                      content: Option[Blob[TTemp]])
    extends TemplateFrag[TType, TTemp, TColor]

object TemplateFrag {
  type Full = TemplateFrag[TemplateType.Full, Template.Full, Color]
}

object StaticFrag {
  type Full = StaticFrag[TemplateType.Full, Template.Full, Color]
}

object FreeTextFrag {
  type Full = FreeTextFrag[TemplateType.Full, Template.Full, Color]

  /**
    * A free text fragment with no text.
    */
  def empty[TType, TTemp, TColor](
      constrainer: RegExp): FreeTextFrag[TType, TTemp, TColor] =
    FreeTextFrag(constrainer, text = "")
}

object Hole {
  type Full = Hole[TemplateType.Full, Template.Full, Color]

  /**
    * A hole with no elements.
    */
  def empty[TType, TTemp, TColor](
      typ: TType,
      isBinding: Boolean): Hole[TType, TTemp, TColor] =
    Hole(typ, isBinding, content = None)
}
