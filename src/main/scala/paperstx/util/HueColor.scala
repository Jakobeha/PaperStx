package paperstx.util

import org.scalajs.dom.ext.Color

/**
  * A general color defined only by hue.
  * It can be specified to different saturations and brightness,
  * but these can be associated since they have the same hue.
  */
case class HueColor(hue: Float) extends AnyVal {
  def specify(saturation: Float, brightness: Float): Color = {
    ColorHelper.fromHSL(this.hue, saturation, brightness)
  }
}

object HueColor {
  //From https://en.wikipedia.org/wiki/Spectral_color
  private val colorHueMap = Map(
    "red" -> 0f,
    "redOrange" -> (12.5f / 360f),
    "orange" -> (25f / 360f),
    "orangeYellow" -> (37.5f / 360f),
    "yellow" -> (50f / 360f),
    "lime" -> (75f / 360f),
    "green" -> (90f / 360f),
    "greenSwamp" -> (125f / 360f),
    "swamp" -> (160f / 360f),
    "turquoise" -> (175f / 360f),
    "ocean" -> (200f / 360f),
    "oceanBlue" -> (220f / 360f),
    "blue" -> (240f / 360f),
    "bluePurple" -> (260f / 360f),
    "purple" -> (280f / 360f),
    "pink" -> (320f / 360f)
  )

  private val colorHues = colorHueMap.values.toSeq

  def parse(text: String): Option[HueColor] =
    colorHueMap.get(text).map(HueColor.apply)

  /**
    * Up to a certain integer, each hue corresponding to a different integer
    * should be relatively distinguishable from the others,
    * and also be relatively easy on the eyes.
    */
  def maxDistinct(seed: Int): HueColor = {
    HueColor(colorHues.apply(Math.abs(seed % colorHues.length)))
  }

  /**
    * A color for the given object using its hash code.
    * The same object will have the same color, generally
    * different objects will have different colors.
    */
  def hashed(value: Any): HueColor = maxDistinct(value.hashCode)
}
