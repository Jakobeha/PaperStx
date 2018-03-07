package paperstx.view.components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.builder.{BuildValidation, TemplateBuilder}

import scalacss.ScalaCssReact._
import scalaz.Scalaz._
import scalaz.{Failure, Success}

object ContentComponent {
  val component =
    ScalaComponent
      .builder[Option[BuildValidation[String]]]("Content")
      .render_P { languageSrc =>
        val content: VdomElement = languageSrc match {
          case None => IntroComponent()
          case Some(languageSrcc) =>
            languageSrcc match {
              case Failure(errors) =>
                ErrorMsgComponent(
                  ("Couldn't read template" <:: errors).distinct.to[Seq])
              case Success(languageSrccc) =>
                TemplateBuilder.build(languageSrccc) match {
                  case Failure(errors) =>
                    ErrorMsgComponent(errors.distinct.to[Seq])
                  case Success(language) => EditorComponent.init(language)
                }
            }
        }

        <.div(
          paperstx.Styles.content,
          content
        )
      }
      .build

  def apply(languageSrc: Option[BuildValidation[String]]): VdomElement =
    component(languageSrc)
}
