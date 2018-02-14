package paperstx.model.block

import paperstx.util.HueColor

case class EnumBlockType(label: String) {
  val color = HueColor.maxDistinct(label.hashCode)
}
