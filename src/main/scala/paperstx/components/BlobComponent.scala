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
            <.input(paperstx.Styles.freeBlob(outerType.colors),
                    ^.`type` := "text",
                    ^.value := content)
          case TemplateBlob(typedTemplate) =>
            <.div(paperstx.Styles.templateBlob,
                  TemplateComponent(typedTemplate))
        }
      }
      .build

  def apply(blob: TypedBlob.Full): VdomElement = component(blob)
}
