package paperstx.model.block

import paperstx.model.phase.Phase

object BlockClass {
  type Full = BlockClass[Phase.Full]

  def apply[TBody](label: String,
                   inPropTypes: Seq[String],
                   outPropTypes: Seq[String],
                   body: TBody): GenBlockClass[TBody] =
    GenBlockClass(label, inPropTypes, outPropTypes, body)

  def unapply[TBody](x: GenBlockClass[TBody])
    : Option[(String, Seq[String], Seq[String], TBody)] =
    GenBlockClass.unapply(x)

  def partitionCases[TPhase <: Phase](elems: Seq[BlockClass[TPhase]])
    : (Seq[EnumBlockClass[TPhase]], Seq[UnionBlockClass[TPhase]]) =
    (elems.flatMap { clazz =>
      clazz.body match {
        case enumBody: EnumClassBody[TPhase @unchecked] =>
          Some(clazz.copy[EnumClassBody[TPhase]](body = enumBody))
        case _ => None
      }
    }, elems.flatMap { clazz =>
      clazz.body match {
        case unionBody: UnionClassBody[TPhase @unchecked] =>
          Some(clazz.copy[UnionClassBody[TPhase]](body = unionBody))
        case _ => None
      }
    })
}

object EnumBlockClass {
  type Full = EnumBlockClass[Phase.Full]
}

object UnionBlockClass {
  type Full = UnionBlockClass[Phase.Full]
}

object EmptyBlockClass {
  type Full = EmptyBlockClass[Phase.Full]
}
