package paperstx.model.block

import scalaz.Functor
import scalaz.Scalaz._

case class GenBlockClass[+TBody](label: String,
                                 inPropTypes: Seq[String],
                                 outPropTypes: Seq[String],
                                 body: TBody) {
  def traverseBody[TNewBody, F[_]: Functor](
      f: TBody => F[TNewBody]): F[GenBlockClass[TNewBody]] = f(body).map {
    GenBlockClass[TNewBody](this.label, this.inPropTypes, this.outPropTypes, _)
  }
}
