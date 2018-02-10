package paperstx

import org.scalajs.dom.document
import paperstx.components.Main
import CssSettings._

object App {
  def main(): Unit = {
    Styles.addToDocument()
    Main().renderIntoDOM(document.getElementById("root"))
  }
}
