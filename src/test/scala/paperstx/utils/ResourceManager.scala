package paperstx.utils

import scalajs.js.Dynamic.global

object ResourceManager {

  /**
    * Reads the text file at the given path, relative to `src/test/resources/`.
    */
  def readTextFile(path: String): String = {
    //From https://stackoverflow.com/questions/40866662/unit-testing-scala-js-read-test-data-from-file-residing-in-test-resources
    val fs = global.require("fs")
    val fullPath = "src/test/resources/" + path
    fs.readFileSync(fullPath).toString
  }
}
