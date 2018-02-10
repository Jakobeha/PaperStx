package paperstx.util

import scala.scalajs.js.RegExp

object RegExpHelper {

  /**
    * A regular expression which fails on every input.
    */
  val failRegExp: RegExp = RegExp("(?!.).")
}
