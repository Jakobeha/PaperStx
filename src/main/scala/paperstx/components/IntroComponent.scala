package paperstx.components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._

import scalacss.ScalaCssReact._

object IntroComponent {
  val component =
    ScalaComponent
      .builder[Unit]("Intro")
      .render_P { errors =>
        <.div(
          paperstx.Styles.intro,
          <.img(paperstx.Styles.logo, ^.src := "images/logo/icon.png"),
          <.div(
            paperstx.Styles.introMsg,
            """
                  |Have you ever seen "block syntax"?
                  |PaperStx allows you to create your own block syntax easily.
                  |Unlike text, block syntax directly the structure of the text.
                  |It makes it harder to form bad expressions.
                  |And it makes it easier to create your own syntax,
                  |because you don't need to worry about e.g. ambiguous parsing.
                """.stripMargin
          )
        )
      }
      .build

  def apply() = component()
}
