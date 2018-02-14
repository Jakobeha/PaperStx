package paperstx.model.block

import paperstx.model.phase.{Phase, PhaseTransformable, PhaseTransformer}
import paperstx.util.TraverseFix._

import scalaz.Applicative
import scalaz.Scalaz._

/**
  * A block type which alters its instances' properties.
  */
case class ReExportType[TPhase <: Phase](
    typ: TPhase#BlockType,
    properties: Seq[BlockClass[TPhase#Dependent]])
    extends PhaseTransformable[ReExportType, TPhase] {
  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
    (transformer.traverseBlockType(typ) |@| properties.traverseF {
      _.traversePhase(transformer.dependent)
    })(ReExportType.apply)
}

object ReExportType {

  /**
    * Used to refer to the original instance's type properties
    * when creating new properties.
    *
    * @example `*>Type` would refer to the original instance's property `Type`.
    */
  val origInstanceBind: String = "*"
}
