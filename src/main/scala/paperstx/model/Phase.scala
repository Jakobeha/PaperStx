package paperstx.model

import paperstx.util.HueColor

trait Phase {
  type TypedTemplate
  type TemplateType
  type Color
}

object Phase {
  trait Full extends Phase {
    override type TypedTemplate = TypedTemplate.Full
    override type TemplateType = TemplateType.Full
    override type Color = HueColor
  }

  trait FullType[TOrig <: Phase] extends Phase {
    import paperstx.model

    override type TypedTemplate = TOrig#TypedTemplate
    override type TemplateType = model.TemplateType[TOrig#Color]
    override type Color = TOrig#Color
  }

  trait Validated extends Phase {
    import paperstx.model

    override type TypedTemplate = Nothing
    override type TemplateType = model.TemplateType[Option[HueColor]]
    override type Color = Option[HueColor]
  }

  trait Parsed extends Phase {
    override type TypedTemplate = Nothing
    override type TemplateType = String
    override type Color = Option[String]
  }
}
