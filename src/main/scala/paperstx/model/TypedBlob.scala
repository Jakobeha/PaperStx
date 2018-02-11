package paperstx.model

case class TypedBlob[TPhase <: Phase](outerType: TemplateType,
                                      blob: Blob[TPhase#TypedTemplate]) {}

object TypedBlob {
  type Full = TypedBlob[Phase.Full]

  /**
    * Gives the `Blob` an undefined type.
    */
  def undefinedType[TPhase <: Phase](blob: Blob[TPhase#TypedTemplate]) =
    TypedBlob(TemplateType.undefined, blob)
}
