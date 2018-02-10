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
          <.div(
            paperstx.Styles.inlineWrapper,
            <.input.text(paperstx.Styles.freeTextFrag(constrainer.test(text)),
                         ^.width := s"${text.length * 16}px",
                         ^.`value` := text)
          )
        case Hole(typ, isBinding, content) =>
          content match {
            case None =>
              <.div(paperstx.Styles.emptyHole,
                    MultiTintComponent(typ.colors.toSeq))
            case Some(blob) =>
              <.div(paperstx.Styles.fullHole,
                    BlobComponent(TypedBlob[Phase.Full](typ, blob)))
          }
      }
      .build

  def apply(templateFrag: TemplateFrag.Full): VdomElement =
    component(templateFrag)
}
