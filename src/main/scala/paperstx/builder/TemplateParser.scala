package paperstx.builder

import fastparse.all._
import paperstx.model.block._
import paperstx.model.local._
import paperstx.util.RegExpHelper

import scala.reflect.ClassTag
import scala.scalajs.js.{JavaScriptException, RegExp, SyntaxError}
import scalaz.{Memo, Success, Validation}

object TemplateParser {

  /**
    * Parses a language with the given contents.
    */
  def parse(str: String): BuildValidation[LocalLanguage] =
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

  private val someNewlinesCapt: Int => P[String] = indentMemo { numIndents =>
    P((("\n" ~ &("\n")).rep.! ~ newlineCapt(numIndents)).map {
      case (x, y) => x + y
    })
  }

  private val instanceLabel: P[String] = P(
    ("*" | CharsWhile(_.isLetterOrDigit)).!.opaque("Instance Label"))
  private val typeLabel: P[String] = P(
    CharsWhile(_.isLetterOrDigit).!.opaque("Type Label"))

  private val staticFrag: Int => P[StaticFrag] = indentMemo { numIndents =>
    P(
      (CharsWhile(!"[]{}\n".contains(_)).! |
        someNewlinesCapt(numIndents) |
        doubleEscape("]") |
        doubleEscape("[") |
        doubleEscape("{") |
        doubleEscape("}"))
        .rep(min = 1)
        .map(_.mkString)
        .map(StaticFrag.apply))
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
              .opaque("Valid Regular Expression")
              .map(_ => RegExpHelper.failRegExp)
        }
    })

  private val freeTextHole: P[FreeTextHole] =
    P(
      ("{" ~/ instanceLabel.? ~ ":" ~ regExp ~ "}")
        .map {
          case (instanceBind, validator) =>
            FreeTextHole.empty(validator, instanceBind)
        })

  private val localType: P[DependentType] = P(
    typeLabel.map(DependentType.apply))

  private val instanceType: P[DependentType] = P(
    (instanceLabel ~ ">" ~/ localType).map {
      case (_instanceLabel, _localType) =>
        _localType.asProperty(_instanceLabel)
    })

  private val dependentType: P[DependentType] = P(instanceType | localType)

  private val functionType: P[FunctionType[DependentType]] = P {
    val rewrite = ("(" ~/ (localType ~/ " = " ~ functionType)
      .rep(min = 1) ~ ")").?.map(
      _.map(_.toMap).map(Rewrite.apply).getOrElse(Rewrite.empty))

    (dependentType ~ rewrite).map {
      case (typ, inputs) => FunctionType(typ, inputs)
    }
  }

  private val blockHole: P[BlockHole] =
    P(("[" ~/ (instanceLabel ~ ":").? ~ functionType ~ "]").map {
      case (instanceBind, _functionType) =>
        BlockHole.empty(_functionType, instanceBind)
    })

  private val blockFrag: Int => P[BlockFrag] =
    indentMemo { numIndents =>
      P(staticFrag(numIndents) | freeTextHole | blockHole)
    }

  //noinspection ForwardReference
  private val subClass: Int => P[LocalBlockClass] =
    indentMemo { numIndents =>
      P("> " ~/ blockClass(numIndents + 1))
    }

  private val block: Int => P[Block] =
    indentMemo { numIndents =>
      P(
        ("-" ~/ ((" " ~/ blockFrag(numIndents + 1).rep) | P("")
          .map(_ => Seq.empty))).map(Block.apply))
    }

  private val localBlock: Int => P[LocalBlock] =
    indentMemo { numIndents =>
      P(
        (block(numIndents) ~ (newline(numIndents) ~ subClass(numIndents)).rep)
          .map {
            case (_block, subClasses) => LocalBlock(_block, subClasses)
          })
    }

  private val unionCase: Int => P[LocalFunctionType] =
    indentMemo { numIndents =>
      P(
        ("| " ~/ functionType ~ (newline(numIndents) ~ subClass(numIndents)).rep)
          .map {
            case (function, subClasses) =>
              LocalFunctionType(function.typ, function.inputs, subClasses)
          })
    }

  private val enumClassBody: Int => P[LocalEnumClassBody] =
    indentMemo { numIndents =>
      P(
        (newline(numIndents) ~ localBlock(numIndents))
          .rep(min = 1)
          .map(LocalEnumClassBody.apply))
    }

  private val unionClassBody: Int => P[LocalUnionClassBody] =
    indentMemo { numIndents =>
      P(
        (newline(numIndents) ~ unionCase(numIndents))
          .rep(min = 1)
          .map(LocalUnionClassBody.apply))
    }

  private val emptyClassBody: P[LocalEmptyClassBody.type] = P(
    P("").map(_ => LocalEmptyClassBody))

  private val classBody: Int => P[LocalClassBody] =
    indentMemo { numIndents =>
      P(enumClassBody(numIndents) | unionClassBody(numIndents) | emptyClassBody)
    }

  private val classHead: Int => P[ClassHead] = indentMemo { numIndents =>
    P(
      (typeLabel ~/
        (newline(numIndents) ~ "< " ~/ dependentType).rep ~
        (newline(numIndents) ~ "> " ~/ dependentType).rep).map {
        case (label, inputs, outputs) => ClassHead(label, inputs, outputs)
      })
  }

  private val blockClass: Int => P[LocalBlockClass] =
    indentMemo { numIndents =>
      P((classHead(numIndents) ~ classBody(numIndents)).map {
        case (head, body) => body.combine(head)
      })
    }

  private val language: P[LocalLanguage] =
    P((blockClass(0).rep(sep = "\n\n") ~/ End).map(LocalLanguage.apply))
}
