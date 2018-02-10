package paperstx.util

import org.scalajs.dom.ext.Color

object ColorHelper {

  /**
    * Up to a certain integer, each color corresponding to a different integer
    * should be easily distinguisable from the others.
    */
  def maxDistinct(seed: Int): Color = {
    fromHSL(seed * 0.12345f, 1, 0.5f)
  }

  def fromHSL(h: Float, s: Float, l: Float): Color = {
    //From https://gist.github.com/mjackson/5311256
    val (rf: Float, gf: Float, bf: Float) = {
      if (s == 0f) {
        (l, l, l)
      } else {
        def hueToRGB(p: Float, q: Float, t: Float): Float = {
          if (t < 0f) {
            hueToRGB(p, q, t + 1f)
          } else if (t > 1f) {
            hueToRGB(p, q, t - 1f)
          } else if (t < 1f / 6f) {
            p + (q - p) * 6f * t
          } else if (t < 1f / 2f) {
            q
          } else if (t < 2f / 3f) {
            p + (q - p) * (2f / 3f - t) * 6f
          } else {
            p
          }
        }

        val q = if (l < 0.5f) { l * (1f + s) } else { l + s - l * s }
        val p = 2f * l - q

        (hueToRGB(p, q, h + 1f / 3f),
         hueToRGB(p, q, h),
         hueToRGB(p, q, h - 1f / 3f))
      }
    }

    Color(r = (rf * 255f).round, g = (gf * 255f).round, b = (bf * 255f).round)
  }
}
