package paperstx.view.props

import japgolly.scalajs.react.Callback

case class Prop[T](get: T, set: T => Callback) {
  def modify(transformer: T => T): Callback = set(transformer(get))

  def promap[T2](fwd: T => T2, bwd: T2 => T): Prop[T2] =
    Prop(fwd(get), set.compose(bwd))

  def narrow[T2](fwd: T => T2, bwd: T => (T2 => T)): Prop[T2] =
    promap(fwd, bwd(get))

  def narrow[T2](fwd: T => T2, bwd: (T, T2) => T): Prop[T2] =
    promap(fwd, { bwd(get, _) })
}

object Prop {
  implicit class SeqProp[T](private val self: Prop[Seq[T]]) {
    def sequence: Seq[Prop[T]] = self.get.zipWithIndex.map {
      case (item, idx) =>
        Prop(item, { newItem: T =>
          self.modify(_.updated(idx, newItem))
        })
    }

    def sequenceWithRewrap: Seq[(Prop[T], T => Seq[T])] =
      self.get.zipWithIndex.map {
        case (item, idx) =>
          def rewrap(newItem: T): Seq[T] = self.get.updated(idx, newItem)

          (Prop(item, { newItem: T =>
            self.set(rewrap(newItem))
          }), rewrap _)
      }
  }

  /** When set, does nothing. */
  def readonly[T](get: T): Prop[T] =
    Prop(get, set = { _ =>
      Callback.empty
    })
}
