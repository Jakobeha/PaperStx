package paperstx

import CssSettings._

object Styles extends StyleSheet.Inline {
  import dsl._

  val physBlobZIndex = 2 //Higher than all other elements

  val mainFont = fontFace("mainFont")(
    _.src("local(Tahoma)").fontStretch.ultraCondensed.fontWeight._200
  )

  val codeFont = fontFace("codeFont")(
    _.src("local(Menlo)")
  )

  val genPassive = style(
    margin(0 px),
    padding(0 px)
  )

  val genMsg = style(
    genPassive,
    textAlign.center,
    fontFamily(mainFont)
  )

  val blockWrapper = style(
    genPassive,
    display.block
  )

  val multiTint = style(
    genPassive,
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
    height(128 px),
    padding(h = 16 px, v = 0 px),
    fontSize(24 px),
  )

  val appTitle = style(
    padding(12 px),
    margin(0 px)
  )

  val langSelect = style(
    padding(8 px)
  )

  val content = style(
    genPassive,
    height :=! "calc(100vh - 128px)"
  )

  val intro = style(
    genMsg
  )

  val introMsg = style(
    fontSize(32 px),
    padding(l = 6.125 %%, r = 6.125 %%, t = 0 px, b = 32 px)
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
    genPassive,
    width(100 %%),
    height(100 %%)
  )

  val fullOverview = style(
    genPassive,
    backgroundColor.rgb(240, 240, 240),
    float.left,
    width(25 %%),
    height :=! "calc(100vh - 128px)",
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
    height(100 %%),
    overflow.visible
  )

  val physBlob = style(genPassive, position.absolute, zIndex(physBlobZIndex))

  val genStxBlock = style(
    fontFamily(codeFont),
    fontSize(16 px),
    borderWidth(1 px),
    borderRadius(8 px),
    borderStyle.solid,
    padding(1 px)
  )

  val freeText = styleF(Domain.boolean *** Domain.boolean) {
    case (isValid, hasBackground) =>
      styleS(
        genStxBlock,
        if (hasBackground) {
          backgroundColor.rgb(224, 224, 224)
        } else {
          backgroundColor.transparent
        },
        color(if (isValid) black else darkred),
        height(14 px),
        if (hasBackground) {
          margin(2 px)
        } else {
          margin(0 px)
        },
        padding(2 px),
        borderWidth(1 px),
        borderColor.rgb(160, 160, 160),
        borderRadius(2 px),
        borderStyle.solid,
        display.inlineBlock
      )
  }

  val freeBlob = style(genPassive, display.inlineBlock, position.relative)

  val blockBlob = style(
    )

  val freeBlobHoleBg = style(
    borderRadius(4 px),
    position.absolute,
    width(100 %%),
    height(100 %%),
    overflow.hidden,
    pointerEvents := "none"
  )

  val block = style(
    genStxBlock,
    borderColor.rgba(128, 128, 128, 0.5),
    borderStyle.solid,
    padding(2 px),
    minWidth(12 px),
    minHeight(18 px),
    display.inlineBlock,
    cursor.pointer,
    userSelect := "none"
  )

  val staticFrag = style(
    margin(0 px),
    padding(0 px),
    display.inline
  )

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
    display.inlineBlock
  )
}
