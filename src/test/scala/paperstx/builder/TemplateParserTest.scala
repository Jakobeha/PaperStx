package paperstx.builder

import paperstx.model.Language
import paperstx.utils.ResourceManager
import utest._

import scalaz.{Failure, Success}

object TemplateParserTest extends TestSuite {
  override def tests = Tests {
    def parseValidLang(filename: String) = {
      val templateString =
        ResourceManager.readTextFile(s"templates/$filename.pstx")
      val valdLanguage = TemplateParser.parse(templateString)
      println(valdLanguage)
      assertMatch(valdLanguage) {
        case Success(_) => ()
      }
      valdLanguage
    }

    def buildValidLang(filename: String) = {
      parseValidLang(filename) match {
        case Success(parsedLang) =>
          val valdResolvedLang = TemplateResolver.resolve(parsedLang)
          println(valdResolvedLang)
          assertMatch(valdResolvedLang) {
            case Success(_) => ()
          }
          valdResolvedLang
        case _ => Failure("Failed to parse")
      }
    }

    buildValidLang("Scheme")
    buildValidLang("Haskell")
  }
}
