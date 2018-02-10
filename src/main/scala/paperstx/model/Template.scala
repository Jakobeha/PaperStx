package paperstx.model

import scalacss.internal.ValueT.Color

case class Template[TType, TTemp, TColor](isBinding: Boolean, frags: Seq[TemplateFrag[TType, TTemp, TColor]]) {

}

object Template {
  type Full = Template[TemplateType.Full, Template.Full, Color]
}