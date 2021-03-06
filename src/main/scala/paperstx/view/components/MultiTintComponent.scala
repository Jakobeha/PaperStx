package paperstx.view.components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.util.HueColor

import scalacss.ScalaCssReact._

object MultiTintComponent {
  val component =
    ScalaComponent
      .builder[Seq[HueColor]]("MultiTint")
      .render_P { colors =>
        <.div(
          paperstx.Styles.multiTint,
          colors.toTagMod { color =>
            <.div(paperstx.Styles.subTint,
                  ^.backgroundColor := color
                    .specify(saturation = 1f, brightness = 0.5f)
                    .toString)
          }
        )
      }
      .build

  def apply(colors: Seq[HueColor]): VdomElement =
    component(colors)
}
