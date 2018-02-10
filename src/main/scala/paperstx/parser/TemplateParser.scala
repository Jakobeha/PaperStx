package paperstx.parser

import fastparse.all._
import paperstx.model._

import scala.scalajs.js.RegExp

object TemplateParser {
  private val typeLabel: P[String] = P(CharsWhile(_.isLetterOrDigit).!)

  val staticFrag: P[StaticFrag[String, String, Option[String]]] =
    P(
      (multiCharsWhile { char =>
        !"[]{}\n".contains(char)
      } |
        doubleEscape("]") |
        doubleEscape("[") |
        doubleEscape("{") |
        doubleEscape("}") |
        "\n  ".!.map { _ =>
          "\n"
        }).map(StaticFrag.apply))

  val freeTextFrag: P[FreeTextFrag[String, String, Option[String]]] =
    P(
      ("{" ~ (CharsWhile(_ != "}") | backslashEscape("}")).! ~ "}")
        .map { RegExp(_) }
        .map(FreeTextFrag.empty))

  val holeBindMode: P[Boolean] =
    P("+".?.!.map(_.nonEmpty))

  val hole: P[Hole[String, String, Option[String]]] =
    P(("[" ~ holeBindMode ~ typeLabel ~ "]").map {
      case (bindMode, typeLabell) => Hole.empty(typeLabell, bindMode)
    })

  val frag: P[TemplateFrag[String, String, Option[String]]] = P(
    staticFrag | freeTextFrag | hole)

  val templateBindMode: P[Boolean] =
    P("+".!.map { _ =>
      true
    } | "-".!.map { _ =>
      false
    })

  val template: P[Template[String, String, Option[String]]] =
    P((templateBindMode ~ " " ~ frag.rep).map {
      case (bindMode, typeLabell) => Template(bindMode, typeLabell)
    })

  val enumClassBody
    : P[String => EnumTemplateClass[String, String, Option[String]]] =
    P(("\n" ~ template.rep(min = 1, sep = "\n")).map { templates =>
      { label =>
        EnumTemplateClass(EnumTemplateType(label, None), templates.toSet)
      }
    })

  val unionClassBody
    : P[String => UnionTemplateClass[String, String, Option[String]]] =
    P((" = " ~ typeLabel.rep(min = 1, sep = " | ")).map { frags =>
      { label =>
        UnionTemplateClass(label, frags.toSet)
      }
    })

  val templateClass: P[TemplateClass[String, String, Option[String]]] =
    P((typeLabel ~ (enumClassBody | unionClassBody)).map {
      case (label, fLabel) => fLabel(label)
    })

  val templateFile: P[Set[TemplateClass[String, String, Option[String]]]] =
    P(templateClass.rep(sep = "\n").map { _.toSet })

  private def multiCharsWhile(pred: Char => Boolean): P[String] =
    P((CharPred(pred).! ~ CharsWhile(pred).!).map { case (x, y) => x + y })

  /**
    * Tries to parse the string escaped by repeating itself.
    * @example `doubleScape("$")` parses `$$` and returns `$`.
    */
  private def doubleEscape(str: String): P[String] =
    P((str + str).!.map { _ =>
      str
    })

  /**
    * Tries to parse the string escaped by a backslash.
    * @example `backslashEscape("$")` parses `\$` and returns `$`.
    */
  private def backslashEscape(str: String): P[String] =
    P(("\\" + str).!.map { _ =>
      str
    })
}
