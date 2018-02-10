package paperstx.model

trait PhaseTransformer[TIn <: Phase, TOut <: Phase, F[_]] {
  def traverseTemplate(template: TIn#Template): F[TOut#Template]
  def traverseTemplateType(templateType: TIn#TemplateType): F[TOut#TemplateType]
  def traverseColor(color: TIn#Color): F[TOut#Color]
}
