package paperstx.model

import paperstx.util.Fix

import scalacss.internal.ValueT.Color

case class Template[TType, TTemp, TColor](
    isBinding: Boolean,
    frags: Seq[TemplateFrag[TType, TTemp, TColor]]) {}

object Template {
  type AlmostFull[TTemp] = Template[TemplateType.Full, TTemp, Color]
  type Full = Fix[AlmostFull]
}
