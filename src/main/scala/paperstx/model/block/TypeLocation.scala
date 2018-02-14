package paperstx.model.block

sealed trait TypeLocation {}

/**
  * Location for a type imported by a class, e.g. `< Type`.
  */
case class ImportTypeLocation(label: String) extends TypeLocation {
  override def toString = label.toString
}

/**
  * Location for an instance's property type, e.g. `instance>Type`.
  */
case class PropTypeLocation(instance: String, prop: String)
    extends TypeLocation {
  override def toString = s"${instance.toString}>${prop.toString}"
}
