package paperstx.model.phase

trait Phase {
  self =>

  type BlockType
  type Dependent <: Phase
}

object Phase {
  trait Full extends Phase {
    import paperstx.model.{block => reg}

    override type BlockType = reg.BlockType
    override type Dependent = FullDependent
  }

  trait FullDependent extends Phase {
    import paperstx.model.{block => reg}

    override type BlockType = reg.DependentType.Full
    override type Dependent = FullDependent
  }

  trait Parsed extends Phase {
    override type BlockType = String
    override type Dependent = Parsed
  }
}
