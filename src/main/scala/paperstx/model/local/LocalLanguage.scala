package paperstx.model.local

import paperstx.model.block.{Language, Scope}

/** Contains local blocks for an AST of a programming language. */
case class LocalLanguage(classes: Seq[LocalBlockClass]) {
  val global: Language = {
    val indexedClasses = classes.zipWithIndex
    //TODO is this the right way to handle dependencies?
    //Ideally this properly resolves classes, except if they refer to each other in weird O(e^n)-resolvable ways.
    val depLessClasses = indexedClasses.map {
      case (localClass, idx) => localClass.globalize(idx, Scope.empty).run._2
    }
    val depLessScope = Scope.absolute(depLessClasses.map(_.typ))
    Language(indexedClasses.flatMap {
      case (localClass, idx) =>
        val (mainClass, extraClasses) =
          localClass.globalize(idx, depLessScope).run
        mainClass :+ extraClasses
    })
  }
}
