package paperstx.model.block

import paperstx.model.phase.{Phase, PhaseTransformable, PhaseTransformer}

import scala.scalajs.js.RegExp
import scalaz.Applicative
import scalaz.Scalaz._

sealed trait BlockFrag[TPhase <: Phase]
    extends PhaseTransformable[BlockFrag, TPhase] {

  def instanceBind: Option[String]

  /**
    * Classes which can be used in other fragments in the same block as this fragment,
    * which come from this fragment.
    */
  def addedClasses: Seq[BlockClass[TPhase#Dependent]]
}

case class StaticFrag[TPhase <: Phase](text: String)
    extends BlockFrag[TPhase]
    with PhaseTransformable[StaticFrag, TPhase] {
  override def instanceBind = None
  override def addedClasses = Seq.empty

  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
    StaticFrag[TNewPhase](this.text).point[F]
}

case class FreeTextHole[TPhase <: Phase](text: String,
                                         validator: RegExp,
                                         instanceBind: Option[String])
    extends BlockFrag[TPhase]
    with PhaseTransformable[FreeTextHole, TPhase] {
  def addedBlock[TPhase2 <: Phase]: Block[TPhase2] = Block.static(text)

  override def addedClasses =
    Seq(
      BlockClass(FreeTextHole.rawPropTypeLabel,
                 inPropTypes = Seq.empty,
                 outPropTypes = Seq.empty,
                 body = EnumClassBody(blocks = Seq(addedBlock))))

  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
    FreeTextHole[TNewPhase](this.text, this.validator, this.instanceBind)
      .point[F]
}

case class BlockHole[TPhase <: Phase](content: Option[Blob[TPhase]],
                                      typ: TPhase#Dependent#BlockType,
                                      instanceBind: Option[String])
    extends BlockFrag[TPhase]
    with PhaseTransformable[BlockHole, TPhase] {
  override def addedClasses =
    content
      .collect {
        case BlockBlob(typedBlock) => typedBlock.block.properties
      }
      .getOrElse(Seq.empty)

  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) = {
    (content.traverse { _.traversePhase(transformer) } |@| transformer.dependent
      .traverseBlockType(typ) |@| instanceBind
      .point[F])(BlockHole.apply[TNewPhase])
  }
}

object BlockFrag {
  type Full = BlockFrag[Phase.Full]
}

object StaticFrag {
  type Full = StaticFrag[Phase.Full]
}

object FreeTextHole {
  type Full = FreeTextHole[Phase.Full]

  val rawPropTypeLabel: String = "Raw"

  /**
    * A free text hole with no text.
    */
  def empty[TPhase <: Phase](
      validator: RegExp,
      instanceBind: Option[String]): FreeTextHole[TPhase] =
    FreeTextHole(text = "", validator, instanceBind)
}

object BlockHole {
  type Full = BlockHole[Phase.Full]

  /**
    * A block hole with no elements.
    */
  def empty[TPhase <: Phase](typ: TPhase#Dependent#BlockType,
                             instanceBind: Option[String]): BlockHole[TPhase] =
    BlockHole(content = None, typ, instanceBind)
}
