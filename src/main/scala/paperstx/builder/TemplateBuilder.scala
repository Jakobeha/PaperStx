package paperstx.builder

import paperstx.model.block.Language

import scalaz.{Failure, Success}

object TemplateBuilder {

  /**
    * Builds a language with the given contents -
    * parses the language, then resolves it.
    */
  def build(str: String): BuildValidation[Language] =
    TemplateParser.parse(str) match {
      case Success(parsedLang) => Success(parsedLang.global)
      case Failure(failures) =>
        Failure("Couldn't build - failed to parse" <:: failures)
    }
}
