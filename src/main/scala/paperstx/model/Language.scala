package paperstx.model

import paperstx.model.block.{
  BlockClass,
  EnumBlockClass,
  EnumBlockType,
  UnionBlockClass
}
import paperstx.model.phase.{Phase, PhaseTransformable, PhaseTransformer}

import scalaz.Applicative
import scalaz.Scalaz._
import paperstx.util.TraverseFix._

/**
  * Contains all the templates for a language
  */
case class Language[TPhase <: Phase](classes: Seq[BlockClass[TPhase]])
    extends PhaseTransformable[Language, TPhase] {
  val (enumClasses, unionClasses): (Seq[EnumBlockClass[TPhase]],
                                    Seq[UnionBlockClass[TPhase]]) =
    BlockClass.partitionCases(classes)

  val unionClassesByLabel: Map[String, Seq[UnionBlockClass[TPhase]]] =
    unionClasses.groupBy { _.label }

  val enumTypes: Seq[EnumBlockType] = enumClasses.map {
    _.enumType
  }
  val enumTypesByLabel: Map[String, Seq[EnumBlockType]] =
    enumTypes.groupBy { _.label }

  def setEnumClasses(
      newEnumClasses: Seq[EnumBlockClass[TPhase]]): Language[TPhase] = {
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
