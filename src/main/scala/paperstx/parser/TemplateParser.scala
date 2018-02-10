package paperstx.parser

import fastparse.all._
import paperstx.model._

object TemplateParser {
  private val typeLabel: P[String] = P(CharsWhile(_.isLetterOrDigit).!)

  val staticFrag: P[StaticFrag[String, String, Option[String]]] =
    P((multiCharsWhile { char => char != '[' && char != ']' && char != '\n' } |
      "]]".!.map { _ => "]" } |
      "[[".!.map { _ => "[" } |
      "\n  ".!.map { _ => "\n" }).map(StaticFrag.apply))

  val hole: P[Hole[String, String, Option[String]]] =
    P(("[" ~ typeLabel.! ~ "]").map(Hole.empty))

  val frag: P[TemplateFrag[String, String, Option[String]]] = P(staticFrag | hole)

  val template: P[Template[String, String, Option[String]]] =
    P(("- " ~ frag.rep).map(Template.apply))

  val enumClassBody: P[String => EnumTemplateClass[String, String, Option[String]]] =
    P(("\n" ~ template.rep(min = 1, sep = "\n")).map { templates => { label =>
      EnumTemplateClass(EnumTemplateType(label, None), templates.toSet)
    } })

  val unionClassBody: P[String => UnionTemplateClass[String, String, Option[String]]] =
    P((" = " ~ typeLabel.rep(min = 1, sep = " | ")).map { frags => { label =>
      UnionTemplateClass(label, frags.toSet)
    } })

  val templateClass: P[TemplateClass[String, String, Option[String]]] =
    P((typeLabel ~ (enumClassBody | unionClassBody)).map {
      case (label, fLabel) => fLabel(label)
    })

  val templateFile: P[Set[TemplateClass[String, String, Option[String]]]] =
    P(templateClass.rep(sep = "\n").map { _.toSet })

  private def multiCharsWhile(pred: Char => Boolean): P[String] =
    P((CharPred(pred).! ~ CharsWhile(pred).!).map { case (x, y) => x + y })
}
