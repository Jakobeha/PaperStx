package paperstx.model

import scala.scalajs.js.RegExp

sealed trait TemplateFrag[TPhase] {}

case class StaticFrag[TPhase <: Phase](text: String)
    extends TemplateFrag[TPhase]

case class FreeTextFrag[TPhase <: Phase](constrainer: RegExp, text: String)
    extends TemplateFrag[TPhase]

case class Hole[TPhase <: Phase](typ: TPhase#TemplateType,
                                 isBinding: Boolean,
                                 content: Option[Blob[TPhase#Template]])
    extends TemplateFrag[TPhase]

object TemplateFrag {
  type Full = TemplateFrag[Phase.Full]
}

object StaticFrag {
  type Full = StaticFrag[Phase.Full]
}

object FreeTextFrag {
  type Full = FreeTextFrag[Phase.Full]

  /**
    * A free text fragment with no text.
    */
  def empty[TPhase <: Phase](constrainer: RegExp): FreeTextFrag[TPhase] =
    FreeTextFrag(constrainer, text = "")
}

object Hole {
  type Full = Hole[Phase.Full]

  /**
    * A hole with no elements.
    */
  def empty[TPhase <: Phase](typ: TPhase#TemplateType,
                             isBinding: Boolean): Hole[TPhase] =
    Hole(typ, isBinding, content = None)
}
