package paperstx.parser

import fastparse.all._
import paperstx.model._
import paperstx.util.RegExpHelper

import scala.scalajs.js.{JavaScriptException, RegExp, SyntaxError}

object TemplateParser {
  private val typeLabel: P[String] = P(CharsWhile(_.isLetterOrDigit).!)

  val staticFrag: P[StaticFrag[String, String, Option[String]]] =
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

  val freeTextFrag: P[FreeTextFrag[String, String, Option[String]]] =
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

  val holeBindMode: P[Boolean] =
    P("+".?.!.map(_.nonEmpty))

  val hole: P[Hole[String, String, Option[String]]] =
    P(("[" ~/ holeBindMode ~ typeLabel ~ "]").map {
      case (bindMode, typeLabell) => Hole.empty(typeLabell, bindMode)
    })

  val frag: P[TemplateFrag[String, String, Option[String]]] =
    P(NoCut(staticFrag | freeTextFrag | hole))

  val templateBindMode: P[Boolean] =
    P("+".!.map { _ =>
      true
    } | "-".!.map { _ =>
      false
    })

  val template: P[Template[String, String, Option[String]]] =
    P((templateBindMode ~ NoCut((" " ~/ frag.rep) | "".!.map { _ =>
      Seq.empty
    })).map {
      case (bindMode, typeLabell) => Template(bindMode, typeLabell)
    })

  val enumClassBody
    : P[String => EnumTemplateClass[String, String, Option[String]]] =
    P(("\n" ~/ template.rep(min = 1, sep = "\n")).map { templates =>
      { label: String =>
        EnumTemplateClass(EnumTemplateType(label, None), templates.toSet)
      }
    })

  val unionClassBody
    : P[String => UnionTemplateClass[String, String, Option[String]]] =
    P((" = " ~/ typeLabel.rep(min = 1, sep = " | ")).map { frags =>
      { label =>
        UnionTemplateClass(label, frags.toSet)
      }
    })

  val templateClass: P[TemplateClass[String, String, Option[String]]] =
    P((typeLabel ~/ NoCut(enumClassBody | unionClassBody)).map {
      case (label, fLabel) => fLabel(label)
    })

  val templateFile: P[Set[TemplateClass[String, String, Option[String]]]] =
    P(templateClass.rep(sep = "\n\n").map { _.toSet } ~/ End)

  /**
    * Tries to parse the string escaped by repeating itself.
    * @example `doubleScape("$")` parses `$$` and returns `$`.
    */
  private def doubleEscape(str: String): P[String] =
    P((str + str).!.map { _ =>
      str
    })
}
