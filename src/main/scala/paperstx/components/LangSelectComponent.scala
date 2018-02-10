package paperstx.components

import japgolly.scalajs.react.{
  Callback,
  CallbackTo,
  ReactEventFromInput,
  ScalaComponent
}
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.raw.FileReader
import scala.collection.mutable
import paperstx.builder.BuildValidation

import scala.concurrent.{Await, Future}
import scalacss.ScalaCssReact._
import scalaz.{Failure, Success, Validation}
import scalaz.Scalaz.{^ => _, _}

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
                  .traverse {
                    _.get
                  } //Either contains every file's text or errors from failed files
                  .map(_.mkString("\n\n")) //If contains every file, combines the text into 1 file
                props.onLangSelect(Some(output)).runNow()
              }
            }

            Callback.traverse(0 until files.length) { index =>
              val file = files.item(index)
              val reader = new FileReader()

              reader.onload = { readEvent =>
                val readerr = readEvent.target.asInstanceOf[FileReader]

                if (readerr.readyState == 2) { //Otherwise not ready yet
                  val output = {
                    if (readerr.error != null) {
                      Validation.failureNel(
                        s"Error while loading file: ${readerr.error.name}")
                    } else {
                      Success(readerr.result.asInstanceOf[String])
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
                     ^.onChange ==> onLangSelectEvent)
      }
      .build

  def apply(onLangSelect: Option[BuildValidation[String]] => Callback) =
    component(Props(onLangSelect))
}
