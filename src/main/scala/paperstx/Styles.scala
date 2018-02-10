package paperstx

import CssSettings._
import org.scalajs.dom.ext.Color

object Styles extends StyleSheet.Inline {
  import dsl._

  val mainFont = fontFace("mainFont")(
    _.src("local(Tahoma)").fontStretch.ultraCondensed.fontWeight._200
  )

  val codeFont = fontFace("codeFont")(
    _.src("local(Menlo)")
  )

  val genSnap = style(
    margin(0 px),
    padding(0 px)
  )

  val genMsg = style(
    genSnap,
    textAlign.center,
    fontFamily(mainFont)
  )

  val blockWrapper = style(
    genSnap,
    display.block
  )

  val inlineWrapper = style(
    genSnap,
    display.inline
  )

  val multiTint = style(
    genSnap,
    width(100 %%),
    height(100 %%),
    display.flex,
    opacity(0.5)
  )

  val subTint = style(
    flexGrow(1)
  )

  val app = style(
    )

  val appHeader = style(
    genMsg,
    backgroundColor.rgb(32, 32, 32),
    color.white,
    height(12.5 %%),
    padding(16 px),
    fontSize(24 px),
  )

  val appTitle = style(
    margin(8 px)
  )

  val langSelect = style(
    padding(8 px)
  )

  val content = style(
    genSnap
  )

  val intro = style(
    genMsg
  )

  val introMsg = style(
    fontSize(32 px),
    padding(h = 6.125 %%, v = 0 px)
  )

  val logo = style(
    width(25 %%),
    height(25 %%),
    padding(3.125 %%)
  )

  val errorMsg = style(
    genMsg
  )

  val errorTitle = style(
    fontSize(42 px)
  )

  val errorDetail = style(
    fontSize(24 px)
  )

  val errorNote = style(
    errorDetail,
    color.rgb(64, 64, 64)
  )

  val editor = style(
    genSnap,
    width(100 %%),
    height(100 %%)
  )

  val fullOverview = style(
    genSnap,
    backgroundColor.rgb(240, 240, 240),
    float.left,
    width(25 %%),
    height(100 %%),
    overflow.scroll
  )

  val classOverview = style(
    padding(v = 4 px, h = 16 px)
  )

  val classHeader = style(
    fontSize(16 px)
  )

  val classOverviewBody = style(
    padding(l = 32 px, r = 0 px, t = 2 px, b = 2 px)
  )

  val basicOverview = style(
    )

  val singleOverview = style(
    display.block,
    padding(h = 0 px, v = 4 px)
  )

  val canvas = style(
    float.right,
    width(75 %%),
    height(100 %%)
  )

  val genStxBlock = style(
    fontFamily(codeFont),
    fontSize(16 px),
    borderWidth(2 px),
    borderRadius(8 px),
    padding(1 px)
  )

  val freeBlob = style(
    genStxBlock,
    color.rgb(240, 240, 240),
    borderColor.rgb(128, 128, 128),
    backgroundColor.rgb(64, 64, 64),
  )

  val templateBlob = style(
    )

  val template = style(
    genStxBlock,
    borderColor.rgba(128, 128, 128, 0.5),
    padding(2 px),
    display.inline
  )

  val staticFrag = style(
    margin(0 px),
    padding(0 px),
    display.inline
  )

  val freeTextFrag = styleF.bool { isValid =>
    styleS(
      color(if (isValid) black else darkred),
      backgroundColor.rgb(224, 224, 224),
      margin(0 px),
      padding(2 px),
      borderWidth(1 px),
      borderColor.rgb(160, 160, 160),
      borderRadius(2 px),
      display.inlineBlock
    )
  }

  val emptyHole = style(
    backgroundImage := "url(./images/textures/fuzz.png)",
    backgroundRepeat := "repeat",
    display.inlineBlock,
    width(20 px),
    height(16 px),
    overflow.hidden,
    borderRadius(4 px),
    verticalAlign.middle
  )

  val fullHole = style(
    display.block
  )
}
