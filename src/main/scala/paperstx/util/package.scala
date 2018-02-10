package paperstx

package object util {
  type Fix[T[_]] = T[Fix[T]]
}
