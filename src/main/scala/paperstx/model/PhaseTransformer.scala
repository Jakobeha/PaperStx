package paperstx.model

trait PhaseTransformer[TIn <: Phase, TOut <: Phase, F[_]] {
  def traverseTemplate(typedTemplate: TIn#TypedTemplate): F[TOut#TypedTemplate]
  def traverseTemplateType(templateType: TIn#TemplateType): F[TOut#TemplateType]
  def traverseColor(color: TIn#Color): F[TOut#Color]
}
