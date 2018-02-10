package paperstx.model

case class TypedBlob[TPhase <: Phase](outerType: TemplateType[TPhase#Color],
                                      blob: Blob[TPhase#TypedTemplate]) {}

object TypedBlob {
  type Full = TypedBlob[Phase.Full]

  /**
    * Gives the `Blob` an undefined type.
    */
  def undefinedType[TPhase <: Phase](blob: Blob[TPhase#TypedTemplate]) =
    TypedBlob(TemplateType.undefined[TPhase#Color], blob)
}
