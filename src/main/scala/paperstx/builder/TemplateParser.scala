package paperstx.builder

import fastparse.all._
import paperstx.model._
import paperstx.model.block._
import paperstx.model.phase.Phase
import paperstx.util.RegExpHelper

import scala.reflect.ClassTag
import scala.scalajs.js.{JavaScriptException, RegExp, SyntaxError}
import scalaz.{Memo, Success, Validation}

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

  /**
    * Files with more indentations parse undefined -
    * really shouldn't have more indentations then this.
    */
  private val maxIndentDepth = Short.MaxValue

  def indentMemo[T >: Null: ClassTag](f: Int => T): Int => T =
    Memo.arrayMemo[T](maxIndentDepth).apply(f)

  /**
    * Tries to parse the string escaped by repeating itself.
    * @example `doubleScape("$")` parses `$$` and returns `$`.
    */
  private def doubleEscape(str: String): P[String] =
    P((str + str).!.map(_ => str))

  private val indent: P[Unit] = P("  ")

  private val newline: Int => P[Unit] = indentMemo { numIndents =>
    P("\n" ~ indent.rep(exactly = numIndents))
  }

  private val newlineCapt: Int => P[String] = indentMemo { numIndents =>
    P(newline(numIndents).map(_ => "\n"))
  }

  private val instanceLabel: P[String] = P(CharsWhile(_.isLetterOrDigit).!)
  private val typeLabel: P[String] = P(CharsWhile(_.isLetterOrDigit).!)

  private val staticFrag: Int => P[StaticFrag[Phase.Parsed]] = indentMemo {
    numIndents =>
      P(
        (CharsWhile(!"[]{}\n".contains(_)).! |
          ("\n".rep.! ~ newlineCapt(numIndents)) |
          doubleEscape("]") |
          doubleEscape("[") |
          doubleEscape("{") |
          doubleEscape("}")).rep(min = 1).map(_.mkString).map(StaticFrag.apply))
  }

  private val regExp: Parser[RegExp] = P(
    (CharsWhile(!"}\\".contains(_)) | ("\\" ~ AnyChar)).rep.!.flatMap {
      regExpStr =>
        try {
          //Start and end conditions are implied
          val implicitRegExpStr = "^" + regExpStr + "$"
          val regExp = RegExp(implicitRegExpStr)
          Pass.map(_ => regExp)
        } catch {
          case JavaScriptException(_: SyntaxError) =>
            Fail
              .opaque("<valid regular expression>")
              .map(_ => RegExpHelper.failRegExp)
        }
    })

  private val freeTextFrag: P[FreeTextHole[Phase.Parsed]] = P(
    ("{" ~/ instanceLabel.? ~ ":" ~ regExp ~ "}")
      .map {
        case (instanceBind, validator) =>
          FreeTextHole.empty(validator, instanceBind)
      })

  private val hole: P[BlockHole[Phase.Parsed]] =
    P(("[" ~/ (instanceLabel ~ ":").? ~ typeLabel ~ "]").map {
      case (instanceBind, typeLabell) =>
        BlockHole.empty[Phase.Parsed](typeLabell, instanceBind)
    })

  private val frag: Int => P[BlockFrag[Phase.Parsed]] = indentMemo {
    numIndents =>
      P(NoCut(staticFrag(numIndents) | freeTextFrag | hole))
  }

  //noinspection ForwardReference
  private val property: Int => P[BlockClass[Phase.Parsed]] = indentMemo {
    numIndents =>
      P("> " ~/ blockClass(numIndents + 1))
  }

  private val block: Int => P[Block[Phase.Parsed]] = indentMemo { numIndents =>
    P(("-" ~/ ((" " ~/ frag(numIndents + 1).rep) | P("")
      .map(_ => Seq.empty)) ~ (newline(numIndents) ~ property(numIndents)).rep)
      .map {
        case (frags, properties) => Block[Phase.Parsed](frags, properties)
      })
  }

  private val unionCase: Int => P[ReExportType[Phase.Parsed]] = indentMemo {
    numIndents =>
      P(
        ("| " ~/ typeLabel ~ (newline(numIndents) ~ property(numIndents)).rep)
          .map {
            case (label, properties) =>
              ReExportType[Phase.Parsed](label, properties)
          })
  }

  private val emptyClassBody: P[EmptyClassBody[Phase.Parsed]] = P(
    "".!.map(_ => EmptyClassBody()))

  private val enumClassBody: Int => P[EnumClassBody[Phase.Parsed]] =
    indentMemo { numIndents =>
      P(
        (newline(numIndents) ~ block(numIndents))
          .rep(min = 1)
          .map(EnumClassBody.apply))
    }

  private val unionClassBody: Int => P[UnionClassBody[Phase.Parsed]] =
    indentMemo { numIndents =>
      P(
        (newline(numIndents) ~ unionCase(numIndents))
          .rep(min = 1)
          .map(_.toSet)
          .map(UnionClassBody.apply))
    }

  private val blockClass: Int => P[BlockClass[Phase.Parsed]] = indentMemo {
    numIndents =>
      P((typeLabel ~/ (newline(numIndents) ~ "< " ~/ typeLabel).rep ~ (newline(
        numIndents) ~ "> " ~/ typeLabel).rep ~ (emptyClassBody | enumClassBody(
        numIndents) | unionClassBody(numIndents))).map {
        case (label, inPropTypes, outPropTypes, body) =>
          BlockClass(label, inPropTypes, outPropTypes, body)
      })
  }

  private val language: P[Language[Phase.Parsed]] =
    P(blockClass(0).rep(sep = "\n\n").map(Language.apply) ~/ End)
}
