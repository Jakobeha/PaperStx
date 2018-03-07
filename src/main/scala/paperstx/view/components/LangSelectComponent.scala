package paperstx.view.components

import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{Callback, ReactEventFromInput, ScalaComponent}
import org.scalajs.dom.raw.FileReader
import paperstx.builder.BuildValidation

import scala.collection.mutable
import scalacss.ScalaCssReact._
import scalaz.Scalaz.{^ => _, _}
import scalaz.{Success, Validation}

object LangSelectComponent {
  case class Props(onLangSelect: Option[BuildValidation[String]] => Callback) {}

  val component =
    ScalaComponent
      .builder[Props]("LangSelect")
      .render_P { props =>
        def onLangSelectEvent(event: ReactEventFromInput): Callback = {
          val files = event.target.files
          if (files.length == 0) {
            props.onLangSelect(None)
          } else {
            val fileOuts: mutable.ArrayBuffer[Option[BuildValidation[String]]] =
              mutable.ArrayBuffer.fill(files.length)(None)

            def addFileOut(index: Int,
                           fileOut: BuildValidation[String]): Unit = {
              fileOuts(index) = Some(fileOut)
              if (fileOuts.forall {
                    _.isDefined
                  }) {
                val output = fileOuts.toList
                  .traverse(_.get) //Either contains every file's text or errors from failed files
                  .map(_.mkString("\n\n")) //If contains every file, combines the text into 1 file
                props.onLangSelect(Some(output)).runNow()
              }
            }

            Callback.traverse(0 until files.length) { index =>
              val file = files.item(index)
              val reader = new FileReader()

              reader.onload = { readEvent =>
                val _reader = readEvent.target.asInstanceOf[FileReader]

                if (_reader.readyState == 2) { //Otherwise not ready yet
                  val output = {
                    if (_reader.error != null) {
                      Validation.failureNel(
                        s"Error while loading file: ${_reader.error.name}")
                    } else {
                      Success(_reader.result.asInstanceOf[String])
                    }
                  }
                  addFileOut(index, output)
                }
              }

              Callback {
                reader.readAsText(file)
              }
            }
          }
        }

        <.input.file(paperstx.Styles.langSelect,
                     ^.multiple := true,
                     ^.onChange ==> onLangSelectEvent _)
      }
      .build

  def apply(onLangSelect: Option[BuildValidation[String]] => Callback) =
    component(Props(onLangSelect))
}
