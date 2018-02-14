package paperstx.model.block

import paperstx.model.phase.{Phase, PhaseTransformable, PhaseTransformer}
import paperstx.util.TraverseFix._

import scalaz.Scalaz._
import scalaz._

case class Block[TPhase <: Phase](frags: Seq[BlockFrag[TPhase]],
                                  properties: Seq[BlockClass[TPhase#Dependent]])
    extends PhaseTransformable[Block, TPhase] {
  lazy val instanceClasses: Map[TypeLocation, BlockClass[TPhase#Dependent]] =
    frags.flatMap { frag =>
      for {
        instanceBind <- frag.instanceBind.toSeq
        addedClass <- frag.addedClasses
      } yield (PropTypeLocation(instanceBind, addedClass.label), addedClass)
    }.toMap

  override def traversePhase[TNewPhase <: Phase, F[_]: Applicative](
      transformer: PhaseTransformer[TPhase, TNewPhase, F]) =
    (frags.traverseF { _.traversePhase(transformer) } |@| properties
      .traverseF { _.traversePhase(transformer.dependent) })(Block.apply)
}

object Block {
  implicit class FullBlock(private val self: Block.Full) {
    def scope(imports: ImportScope.Full): BlockScope.Full = {
      val importTypes = imports.typesByLabel.mapKeys(ImportTypeLocation.apply)

      //Tries every dependency combination,
      //might take long, but no recursive references and any
      //instance property can reference any other instance property.
      def peekScope(peekingLocs: Set[TypeLocation]): BlockScope.Full = {
        val instanceTypes: Map[TypeLocation, BlockType] =
          self.instanceClasses
          //A cycle would occur -- already peeking for this class
            .filterKeys { !peekingLocs.contains(_) }
            .map {
              case (instanceLoc, instanceClass) =>
                val instancePeekScope =
                  peekScope(peekingLocs = peekingLocs + instanceLoc)
                val instanceType = instanceClass.specifyType(instancePeekScope)
                (instanceLoc, instanceType)
            }

        BlockScope[Phase.Full](typesByLocation = importTypes ++ instanceTypes)
      }

      peekScope(peekingLocs = Set.empty)
    }
  }

  type Full = Block[Phase.Full]

  /**
    * Creates a block which encodes and renders the given text,
    * and has no holes or properties.
    */
  def static[TPhase <: Phase](text: String): Block[TPhase] =
    Block(frags = Seq(StaticFrag(text)), properties = Seq.empty)
}
