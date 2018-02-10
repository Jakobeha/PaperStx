package paperstx.builder

import paperstx.model._
import paperstx.util.{ColorHelper, HueColor}

import scalaz.Scalaz._
import scalaz._

object TemplateResolver {
  private case class ValidationResolver(context: Language[Phase.Parsed])
      extends PhaseTransformer[Phase.Parsed, Phase.Validated, BuildValidation] {
    override def traverseTemplate(typedTemplate: Nothing) = typedTemplate

    override def traverseTemplateType(typeLabel: String) = {
      val ambiguousFailure = Validation.failureNel(
        s"Type is ambiguous - found multiple times: $typeLabel")
      val notFoundFailure = Validation.failureNel(s"Type not found: $typeLabel")

      val enumType = context.enumTypesByLabel.get(typeLabel).flatMap {
        templateTypes =>
          templateTypes.toList match {
            case Nil => None
            case templateType :: Nil =>
              Some(
                TemplateType
                  .lift(templateType)
                  .traverseColor(this.traverseColor))
            case _ =>
              Some(ambiguousFailure)
          }
      }

      lazy val getUnionType =
        context.unionClassesByLabel.get(typeLabel).flatMap { unionClasses =>
          unionClasses.toList match {
            case Nil => None
            case unionClass :: Nil =>
              Some(unionClass.traversePhase(this).map {
                new TemplateClass.FullTypeTemplateClass(_).typ
              })
            case _ =>
              Some(Validation.failureNel(ambiguousFailure))
          }
        }

      enumType
        .orElse(getUnionType)
        .getOrElse(notFoundFailure)
        .asInstanceOf[BuildValidation[TemplateType[Option[HueColor]]]]
    }

    override def traverseColor(colorStr: Option[String]) = colorStr match {
      case Some(colorStrr) =>
        HueColor.parse(colorStrr) match {
          case Some(color) => Success(Some(color))
          case None        => Validation.failureNel(s"Not a valid color: $colorStrr")
        }
      case None => Success(None)
    }
  }

  /**
    * Resolves cases where the same text will resolve to different values,
    * e.g. 2 different templates without defined colors get different defined colors.
    */
  private object DifferResolve
      extends PhaseTransformer[Phase.Validated, Phase.Full, Differ] {
    override def traverseTemplate(typedTemplate: Nothing) = typedTemplate

    override def traverseTemplateType(
        templateType: TemplateType[Option[HueColor]])
      : Differ[TemplateType[HueColor]] =
      templateType.traverseColor(this.traverseColor)

    override def traverseColor(color: Option[HueColor]): Differ[HueColor] =
      color match {
        case Some(colorr) => colorr.point[Differ]
        case None =>
          for {
            newColorSeed <- get[Int]
            _ <- modify[Int](_ + 1)
          } yield HueColor.maxDistinct(newColorSeed)
      }
  }

  private type Differ[T] = State[Int, T]

  def resolve(
      language: Language[Phase.Parsed]): BuildValidation[Language[Phase.Full]] =
    resolve[Language[Phase.Parsed], Language, Language](language, language)

  def resolve[TIn <: PhaseTransformable[TInter, Phase.Parsed],
              TInter[T <: Phase] <: PhaseTransformable[TOut, T],
              TOut[_ <: Phase]](context: Language[Phase.Parsed],
                                x: TIn): BuildValidation[TOut[Phase.Full]] = {
    (x.traversePhase(ValidationResolver(context)): BuildValidation[
      TInter[Phase.Validated]]).map {
      _.traversePhase(DifferResolve).eval(0)
    }
  }
}
