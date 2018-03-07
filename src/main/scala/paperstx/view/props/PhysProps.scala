package paperstx.view.props

import japgolly.scalajs.react.{Callback, ReactDragEventFromHtml}
import paperstx.model.block.{Blob, Scope}
import paperstx.model.physical.HoleOp

case class PhysProps[TModel](
    rootScope: Scope,
    depScope: Scope,
    solidify: String => Blob,
    onDragStart: (ReactDragEventFromHtml, Blob) => Callback,
    onFillOrEmpty: HoleOp[TModel] => Callback) {
  val fullScope: Scope = rootScope ++ depScope

  def narrowRescope[TModel2](newDepScope: Scope,
                             rewrap: TModel2 => TModel): PhysProps[TModel2] =
    PhysProps(rootScope,
              newDepScope,
              solidify,
              onDragStart,
              HoleOp.narrow(onFillOrEmpty, rewrap))

  def narrow[TModel2](rewrap: TModel2 => TModel): PhysProps[TModel2] =
    narrowRescope(depScope, rewrap)
}
