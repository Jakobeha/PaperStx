package paperstx.model

import paperstx.util.HueColor

trait Phase {
  type TypedTemplate
  type TemplateType
}

object Phase {
  trait Full extends Phase {
    override type TypedTemplate = TypedTemplate.Full
    override type TemplateType = TemplateType.Full
  }

  trait FullType[TOrig <: Phase] extends Phase {
    import paperstx.model

    override type TypedTemplate = TOrig#TypedTemplate
    override type TemplateType = model.TemplateType
  }

  trait Parsed extends Phase {
    override type TypedTemplate = Nothing
    override type TemplateType = String
  }
}
