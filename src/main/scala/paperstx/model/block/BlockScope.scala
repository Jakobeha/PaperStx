package paperstx.model.block

import paperstx.model.phase.Phase

/**
  * Scope containing class imports (`< Type`) and instance properties (`instance>Type`).
  */
case class BlockScope[TPhase <: Phase](
    typesByLocation: Map[TypeLocation, TPhase#BlockType]) {
  def typeForLocation(location: TypeLocation): Option[TPhase#BlockType] =
    this.typesByLocation.get(location)
}

object BlockScope {
  type Full = BlockScope[Phase.Full]
}
