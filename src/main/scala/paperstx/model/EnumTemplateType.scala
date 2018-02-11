package paperstx.model

import paperstx.util.HueColor

case class EnumTemplateType(label: String) {
  val color = HueColor.maxDistinct(label.hashCode)
}
