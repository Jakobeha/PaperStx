package paperstx

import CssSettings._
import org.scalajs.dom.ext.Color

object Styles extends StyleSheet.Inline {
  import dsl._

  val mainFont = fontFace("mainFont")(
    _.src("local(Tahoma)").fontStretch.ultraCondensed.fontWeight._200
  )

  val appHeader = style(
    backgroundColor.rgb(34, 34, 34),
    height(150 px),
    padding(20 px),
    color.white
  )

  val appLogo = style(
    height(80 px)
  )

  val appIntro = style(
    fontSize.large
  )

  val app = style(
    textAlign.center,
    fontFamily(mainFont)
  )

  val langSelect = style(
    )

  val content = style(
    margin(0 px),
    padding(0 px)
  )

  val intro = style(
    )

  val introTitle = style(
    )

  val introMsg = style(
    )

  val errorMsg = style(
    )

  val errorTitle = style(
    )

  val errorDetail = style(
    )

  val errorNote = style(
    )

  val editor = style(
    )

  val fullOverview = style(
    )

  val classOverview = style(
    )

  val classHeader = style(
    )

  val classOverviewBody = style(
    )

  val basicOverview = style(
    )

  val canvas = style(
    )

  def freeBlob(colors: Set[Color]) = style(
    )

  val templateBlob = style(
    )

  def template(color: Color) = style(
    )

  val staticFrag = style(
    )

  def freeTextFrag(valid: Boolean) = style(
    )

  def emptyHole(colors: Set[Color]) = style(
    )

  val fullHole = style(
    )
}
