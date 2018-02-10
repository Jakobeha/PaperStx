package paperstx.model

import paperstx.util.Fix

trait Phase {
  type Template
  type TemplateType
  type Color
}

object Phase {
  trait Full extends Phase {
    override type Template = Template.Full
    override type TemplateType = TemplateType.Full
    override type Color = scalacss.internal.ValueT.Color
  }

  trait FullType[TOrig <: Phase] extends Phase {
    override type Template = TOrig#Template
    override type TemplateType = paperstx.model.TemplateType[TOrig#Color]
    override type Color = TOrig#Color
  }

  trait Parsed extends Phase {
    override type Template = String
    override type TemplateType = String
    override type Color = Option[String]
  }
}
