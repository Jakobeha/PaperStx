package paperstx.model

/**
  * Determines whether or not an object could fit in a [[paperstx.model.Hole]].
  */
case class HoleSkeleton(typ: TemplateType.Full, isBinding: Boolean)
