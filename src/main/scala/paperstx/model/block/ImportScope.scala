package paperstx.model.block

import paperstx.model.phase.Phase

/**
  * Scope containing only class imports (`< Type`).
  */
case class ImportScope[TPhase <: Phase](
    typesByLabel: Map[String, TPhase#BlockType])

object ImportScope {
  def empty[TPhase <: Phase]: ImportScope[TPhase] =
    ImportScope(typesByLabel = Map.empty)

  type Full = ImportScope[Phase.Full]
}
