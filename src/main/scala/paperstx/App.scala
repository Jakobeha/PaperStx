package paperstx

import org.scalajs.dom.document
import paperstx.components.MainComponent
import CssSettings._

object App {
  def main(): Unit = {
    Styles.addToDocument()
    MainComponent().renderIntoDOM(document.getElementById("root"))
  }
}
