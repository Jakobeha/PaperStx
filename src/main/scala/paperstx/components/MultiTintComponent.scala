package paperstx.components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.ext.Color
import paperstx.model._
import paperstx.util.ColorHelper

import scalacss.ScalaCssReact._

object MultiTintComponent {
  val component =
    ScalaComponent
      .builder[Seq[Color]]("MultiTint")
      .render_P { colors =>
        <.div(paperstx.Styles.multiTint, colors.toTagMod { color =>
          <.div(paperstx.Styles.subTint, ^.backgroundColor := color.toString)
        })
      }
      .build

  def apply(colors: Seq[Color]): VdomElement =
    component(colors)
}
