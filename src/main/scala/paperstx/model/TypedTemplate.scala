package paperstx.model

case class TypedTemplate[TPhase <: Phase](typ: EnumTemplateType[TPhase#Color],
                                          template: Template[TPhase]) {

  /**
    * Whether the template can fill the hole.
    */
  def fitsIn(hole: Hole[Phase.FullType[TPhase]]): Boolean =
    hole.typ.isSuperset(this.typ) && hole.isBinding == template.isBinding
}
