package paperstx.components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model._
import scalacss.ScalaCssReact._

object TemplateComponent {
  val component =
    ScalaComponent
      .builder[TypedTemplate.Full]("Template")
      .render_P { typedTemplate =>
        val typ = typedTemplate.typ
        val template = typedTemplate.template

        <.div(paperstx.Styles.template,
              ^.backgroundColor := typ.color
                .specify(saturation = 0.75f, brightness = 0.5f)
                .toString,
              template.frags.toTagMod(FragComponent.apply))
      }
      .build

  def apply(typedTemplate: TypedTemplate.Full): VdomElement =
    component(typedTemplate)
}
