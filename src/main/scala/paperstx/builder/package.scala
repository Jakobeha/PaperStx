package paperstx

import scalaz.{ValidationNel}

package object builder {

  /**
    * The result of a build, or 1 step in a build.
    */
  type BuildValidation[+T] = ValidationNel[String, T]
}
