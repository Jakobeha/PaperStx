package paperstx.components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._
import scalacss.ScalaCssReact._

object ClassHeaderComponent {
  val component =
    ScalaComponent
      .builder[String]("ClassHeader")
      .render_P { label =>
        <.div(
          paperstx.Styles.classHeader,
          label
        )
      }
      .build

  def apply(label: String): VdomElement = component(label)
}
