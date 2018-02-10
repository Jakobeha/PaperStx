package paperstx.builder

import paperstx.model.Language

import scalaz.{Failure, Success}

object TemplateBuilder {

  /**
    * Builds a language with the given contents -
    * parses the language, then resolves it.
    */
  def build(str: String): BuildValidation[Language.Full] =
    TemplateParser.parse(str) match {
      case Success(parsedLang) =>
        TemplateResolver.resolve(parsedLang) match {
          case Success(parsedLang) => Success(parsedLang)
          case Failure(failures) =>
            Failure("Couldn't build - failed to resolve" <:: failures)
        }
      case Failure(failures) =>
        Failure("Couldn't build - failed to parse" <:: failures)
    }
}
