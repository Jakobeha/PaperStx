package paperstx.model

case class TypedTemplate[TType, TTemp, TColor](typ: EnumTemplateType[TColor],
                                               template: Template[TType, TTemp, TColor]) {
  /**
    * Whether the template can fill the hole.
    */
  def fitsIn(hole: Hole[TemplateType[TColor], TTemp, TColor]): Boolean =
    hole.typ.isSuperset(this.typ) && hole.isBinding == template.isBinding
}
