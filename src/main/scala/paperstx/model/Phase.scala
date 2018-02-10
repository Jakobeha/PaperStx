package paperstx.model

trait Phase {
  type TypedTemplate
  type TemplateType
  type Color
}

object Phase {
  trait Full extends Phase {
    import org.scalajs.dom.ext

    override type TypedTemplate = TypedTemplate.Full
    override type TemplateType = TemplateType.Full
    override type Color = ext.Color
  }

  trait FullType[TOrig <: Phase] extends Phase {
    import paperstx.model

    override type TypedTemplate = TOrig#TypedTemplate
    override type TemplateType = model.TemplateType[TOrig#Color]
    override type Color = TOrig#Color
  }

  trait Validated extends Phase {
    import paperstx.model
    import org.scalajs.dom.ext

    override type TypedTemplate = Nothing
    override type TemplateType = model.TemplateType[Option[ext.Color]]
    override type Color = Option[ext.Color]
  }

  trait Parsed extends Phase {
    override type TypedTemplate = Nothing
    override type TemplateType = String
    override type Color = Option[String]
  }
}
