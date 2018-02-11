package paperstx.model

import scalaz.Applicative
import scalaz.Scalaz._
import paperstx.util.TraverseFix._

/**
  * Contains all the templates for a language
  */
case class Language[TPhase <: Phase](classes: Seq[TemplateClass[TPhase]])
    extends PhaseTransformable[Language, TPhase] {
  val (enumClasses, unionClasses): (Seq[EnumTemplateClass[TPhase]],
                                    Seq[UnionTemplateClass[TPhase]]) =
    TemplateClass.partitionCases(classes)

  val unionClassesByLabel: Map[String, Seq[UnionTemplateClass[TPhase]]] =
    unionClasses.groupBy { _.label }

  val enumTypes: Seq[EnumTemplateType] = enumClasses.map {
    _.enumType
  }
  val enumTypesByLabel: Map[String, Seq[EnumTemplateType]] =
    enumTypes.groupBy { _.label }

  def setEnumClasses(
      newEnumClasses: Seq[EnumTemplateClass[TPhase]]): Language[TPhase] = {
    Language(classes = newEnumClasses ++ unionClasses)
  }

  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
    classes
      .traverseF { _.traversePhase(transformer) }
      .map(Language.apply)

}

object Language {
  type Full = Language[Phase.Full]

  def empty[TPhase <: Phase]: Language[TPhase] = Language(classes = Seq.empty)
}
