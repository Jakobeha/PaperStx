package paperstx.builder

import paperstx.model._
import paperstx.util.{ColorHelper, HueColor}

import scalaz.Scalaz._
import scalaz._

object TemplateResolver {
  private case class Resolver(context: Language[Phase.Parsed])
      extends PhaseTransformer[Phase.Parsed, Phase.Full, BuildValidation] {
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
              Some(Success(TemplateType.lift(templateType)))
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
        .asInstanceOf[BuildValidation[TemplateType]]
    }
  }

  def resolve(
      language: Language[Phase.Parsed]): BuildValidation[Language[Phase.Full]] =
    resolve[Language[Phase.Parsed], Language](language, language)

  def resolve[TIn <: PhaseTransformable[TOut, Phase.Parsed], TOut[_ <: Phase]](
      context: Language[Phase.Parsed],
      x: TIn): BuildValidation[TOut[Phase.Full]] = {
    x.traversePhase(Resolver(context))
  }
}
