package paperstx.util

object ColExts {
  implicit class MapExt[K, +V](private val self: Map[K, V]) {
    def mapMaybeValues[V2](f: V => Option[V2]): Map[K, V2] = self.flatMap {
      case (key, value) => f(value).map((key, _))
    }
  }
}
