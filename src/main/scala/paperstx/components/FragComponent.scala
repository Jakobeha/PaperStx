package paperstx.components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model._
import scalacss.ScalaCssReact._

object FragComponent {
  val component =
    ScalaComponent
      .builder[TemplateFrag.Full]("Frag")
      .render_P {
        case StaticFrag(text) => <.pre(paperstx.Styles.staticFrag, text)
        case FreeTextFrag(constrainer, text) =>
          <.input(
            paperstx.Styles.freeTextFrag(valid = constrainer.test(text)),
            ^.`type` := "text",
            ^.`value` := text
          )
        case Hole(typ, isBinding, content) =>
          content match {
            case None => <.div(paperstx.Styles.emptyHole(typ.colors))
            case Some(blob) =>
              <.div(paperstx.Styles.fullHole,
                    BlobComponent(TypedBlob[Phase.Full](typ, blob)))
          }
      }
      .build

  def apply(templateFrag: TemplateFrag.Full): VdomElement =
    component(templateFrag)
}
