package paperstx

import org.scalajs.dom.document
import paperstx.components.Main

import scala.scalajs.js.JSApp

object App extends JSApp {
  def main(): Unit = {
    Styles.addToDocument()
    Main().renderIntoDOM(document.getElementById("root"))
  }
}
