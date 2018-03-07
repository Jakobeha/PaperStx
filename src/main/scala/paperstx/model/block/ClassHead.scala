package paperstx.model.block

/** General information about a class - all classes (even local ones) contain this. */
case class ClassHead(label: String,
                     inputs: Seq[DependentType],
                     outputs: Seq[DependentType])
