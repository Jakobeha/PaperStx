package paperstx.builder

import fastparse.all._
import paperstx.model._
import paperstx.util.RegExpHelper

import scala.scalajs.js.{JavaScriptException, RegExp, SyntaxError}
import scalaz.{Failure, Success, Validation}

object TemplateParser {

  /**
    * Parses a language with the given contents.
    */
  def parse(str: String): BuildValidation[Language[Phase.Parsed]] =
    language.parse(str) match {
      case Parsed.Success(res, _) => Success(res)
      case fail: Parsed.Failure =>
        Validation.failureNel(ParseError(fail).getMessage)
    }

  private val typeLabel: P[String] = P(CharsWhile(_.isLetterOrDigit).!)

  private val staticFrag: P[StaticFrag[Phase.Parsed]] =
    P(
      (CharsWhile { char =>
        !"[]{}\n".contains(char)
      }.! |
        doubleEscape("]") |
        doubleEscape("[") |
        doubleEscape("{") |
        doubleEscape("}") |
        "\n  ".!.map { _ =>
          "\n"
        }).rep(min = 1).map(_.mkString).map(StaticFrag.apply))

  private val freeTextFrag: P[FreeTextFrag[Phase.Parsed]] =
    P(
      ("{" ~/ (CharsWhile(!"}\\".contains(_)) | ("\\" ~ AnyChar)).rep.! ~ "}")
        .flatMap { regExpStr =>
          try {
            val regExp = RegExp(regExpStr)
            Pass.map { _ =>
              regExp
            }
          } catch {
            case JavaScriptException(_: SyntaxError) =>
              Fail.opaque("<valid regular expression>").map { _ =>
                RegExpHelper.failRegExp
              }
          }
        }
        .map(FreeTextFrag.empty))

  private val holeBindMode: P[Boolean] =
    P("+".?.!.map(_.nonEmpty))

  private val hole: P[Hole[Phase.Parsed]] =
    P(("[" ~/ holeBindMode ~ typeLabel ~ "]").map {
      case (bindMode, typeLabell) =>
        Hole.empty[Phase.Parsed](typeLabell, bindMode)
    })

  private val frag: P[TemplateFrag[Phase.Parsed]] =
    P(NoCut(staticFrag | freeTextFrag | hole))

  private val templateBindMode: P[Boolean] =
    P("+".!.map { _ =>
      true
    } | "-".!.map { _ =>
      false
    })

  private val template: P[Template[Phase.Parsed]] =
    P((templateBindMode ~ NoCut((" " ~/ frag.rep) | "".!.map { _ =>
      Seq.empty
    })).map {
      case (bindMode, typeLabell) => Template(bindMode, typeLabell)
    })

  private val enumClassBody: P[String => EnumTemplateClass[Phase.Parsed]] =
    P(("\n" ~/ template.rep(min = 1, sep = "\n")).map { templates =>
      { label: String =>
        EnumTemplateClass[Phase.Parsed](EnumTemplateType(label, None),
                                        templates.toSet)
      }
    })

  private val unionClassBody: P[String => UnionTemplateClass[Phase.Parsed]] =
    P((" = " ~/ typeLabel.rep(min = 1, sep = " | ")).map { frags =>
      { label =>
        UnionTemplateClass[Phase.Parsed](label, frags.toSet)
      }
    })

  private val templateClass: P[TemplateClass[Phase.Parsed]] =
    P((typeLabel ~/ NoCut(enumClassBody | unionClassBody)).map {
      case (label, fLabel) => fLabel(label)
    })

  private val language: P[Language[Phase.Parsed]] =
    P(
      templateClass
        .rep(sep = "\n\n")
        .map { _.toSet }
        .map(Language.apply) ~/ End)

  /**
    * Tries to parse the string escaped by repeating itself.
    * @example `doubleScape("$")` parses `$$` and returns `$`.
    */
  private def doubleEscape(str: String): P[String] =
    P((str + str).!.map { _ =>
      str
    })
}
