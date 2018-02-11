package paperstx.model

case class TypedTemplate[TPhase <: Phase](typ: EnumTemplateType,
                                          template: Template[TPhase]) {}

object TypedTemplate {
  implicit class FullTypedTemplate(private val self: TypedTemplate.Full) {

    /**
      * Whether the template can fill the hole with the given skeleton.
      */
    def fitsIn(skeleton: HoleSkeleton): Boolean =
      skeleton.typ.isSuperset(self.typ) && skeleton.isBinding == self.template.isBinding
  }

  type Full = TypedTemplate[Phase.Full]
}
