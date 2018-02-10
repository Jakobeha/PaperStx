package paperstx.model
import scalaz.Applicative
import scalaz.Scalaz._
import paperstx.util.TraverseFix._

/**
  * Contains all the templates for a language
  */
case class Language[TPhase <: Phase](classes: Set[TemplateClass[TPhase]])
    extends PhaseTransformable[Language, TPhase] {
  val (enumClasses, unionClasses): (Set[EnumTemplateClass[TPhase]],
                                    Set[UnionTemplateClass[TPhase]]) =
    TemplateClass.partitionCases(classes)

  val unionClassesByLabel: Map[String, Set[UnionTemplateClass[TPhase]]] =
    unionClasses.groupBy { _.label }

  val enumTypes: Set[EnumTemplateType[TPhase#Color]] = enumClasses.map {
    _.enumType
  }
  val enumTypesByLabel: Map[String, Set[EnumTemplateType[TPhase#Color]]] =
    enumTypes.groupBy { _.label }

  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
    classes
      .traverseF { _.traversePhase(transformer) }
      .map(Language.apply[TNewPhase] _)

}

object Language {
  type Full = Language[Phase.Full]
}
