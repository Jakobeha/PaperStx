package paperstx.view.props

import scala.scalajs.js.RegExp

sealed trait FreeTextForm {
  val validator: Option[RegExp]

  val hasBackground: Boolean

  val autoFocus: Boolean
}

case class FreeFragForm(validatorr: RegExp) extends FreeTextForm {
  override val validator = Some(validatorr)

  override val hasBackground = true

  override val autoFocus = false
}

case object FreeBlobForm extends FreeTextForm {
  override val validator = None

  override val hasBackground = false

  override val autoFocus = true
}
