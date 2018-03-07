package paperstx.builder

import paperstx.utils.ResourceManager
import utest._

import scalaz.{Failure, Success}

object TemplateParserTest extends TestSuite {
  override def tests = Tests {
    def buildValidLang(filename: String) = {
      parseValidLang(filename) match {
        case Success(language) =>
          println(s"$filename - Built Language:")
          println(language.global)
        case Failure(_) => ()
      }
    }

    def parseValidLang(filename: String) = {
      val templateString =
        ResourceManager.readTextFile(s"templates/$filename.pstx")
      val valdLanguage = TemplateParser.parse(templateString)
      println(s"$filename - Parsed Language:")
      println(valdLanguage)
      assertMatch(valdLanguage) {
        case Success(_) => ()
      }
      valdLanguage
    }

    buildValidLang("Basic")
    buildValidLang("Java")
  }
}
