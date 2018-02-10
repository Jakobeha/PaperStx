package paperstx.components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.model._

import scalacss.ScalaCssReact._

object BlobComponent {
  val component =
    ScalaComponent
      .builder[TypedBlob.Full]("Blob")
      .render_P { typedBlob =>
        val outerType = typedBlob.outerType
        val blob = typedBlob.blob

        blob match {
          case FreeBlob(content) =>
            <.div(
              paperstx.Styles.inlineWrapper,
              <.input.text(paperstx.Styles.freeBlob,
                           VdomStyle("width") := (content.length * 16) + " px",
                           ^.value := content,
                           MultiTintComponent(outerType.colors.toSeq))
            )
          case TemplateBlob(typedTemplate) =>
            <.div(paperstx.Styles.templateBlob,
                  TemplateComponent(typedTemplate))
        }
      }
      .build

  def apply(blob: TypedBlob.Full): VdomElement = component(blob)
}
