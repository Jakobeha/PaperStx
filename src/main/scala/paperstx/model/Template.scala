package paperstx.model

import paperstx.util.Fix

import scalacss.internal.ValueT.Color

case class Template[TPhase <: Phase](isBinding: Boolean,
                                     frags: Seq[TemplateFrag[TPhase]]) {}

object Template {
  type Full = Template[Phase.Full]
}
