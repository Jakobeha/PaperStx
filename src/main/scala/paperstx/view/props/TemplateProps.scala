package paperstx.view.props

import japgolly.scalajs.react.{Callback, ReactDragEventFromHtml}
import paperstx.model.block.{Blob, Scope, TypedBlock}

case class TemplateProps(
    scope: Scope,
    solidify: String => Blob,
    onDragStart: (ReactDragEventFromHtml, Blob) => Callback) {
  lazy val toPhysProps: PhysProps[TypedBlock] =
    PhysProps(
      scope,
      depScope = Scope.empty,
      solidify,
      onDragStart,
      //Templates can't be filled or emptied, like they can't be dragged around.
      //This enforces that rule.
      onFillOrEmpty = { _ =>
        Callback.empty
      }
    )
}
