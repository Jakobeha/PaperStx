package paperstx.components

import japgolly.scalajs.react.{Callback, ReactDragEventFromHtml, ScalaComponent}
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model._

import scalacss.ScalaCssReact._

object FragComponent {
  case class Props(
      templateFrag: TemplateFrag.Full,
      onTemplateFragChange: TemplateFrag.Full => Callback,
      onDragStart: (ReactDragEventFromHtml, TypedTemplate.Full) => Callback)

  val component =
    ScalaComponent
      .builder[Props]("Frag")
      .render_P { props =>
        val templateFrag = props.templateFrag
        val onTemplateFragChange = props.onTemplateFragChange
        val onDragStart = props.onDragStart

        templateFrag match {
          case StaticFrag(text) => <.pre(paperstx.Styles.staticFrag, text)
          case FreeTextFrag(validator, text) =>
            FreeTextComponent(
              text,
              Some(validator),
              hasBackground = true,
              onTextChange = { newFreeText =>
                onTemplateFragChange(FreeTextFrag(validator, newFreeText))
              })
          case Hole(typ, isBinding, content) =>
            content match {
              case None =>
                <.div(paperstx.Styles.emptyHole,
                      MultiTintComponent(typ.colors.toSeq))
              case Some(blob) =>
                <.div(
                  paperstx.Styles.fullHole,
                  BlobComponent(
                    TypedBlob[Phase.Full](typ, blob),
                    onBlobChange = { newBlob =>
                      onTemplateFragChange(
                        Hole[Phase.Full](typ, isBinding, Some(newBlob)))
                    },
                    onDragStart
                  )
                )
            }
        }
      }
      .build

  def apply(templateFrag: TemplateFrag.Full,
            onTemplateFragChange: TemplateFrag.Full => Callback,
            onDragStart: (ReactDragEventFromHtml,
                          TypedTemplate.Full) => Callback): VdomElement =
    component(Props(templateFrag, onTemplateFragChange, onDragStart))
}
