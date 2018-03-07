package paperstx

import org.scalajs.dom.document
import CssSettings._
import paperstx.view.components.MainComponent

object App {
  def main(): Unit = {
    Styles.addToDocument()
    MainComponent().renderIntoDOM(document.getElementById("root"))
  }
}
