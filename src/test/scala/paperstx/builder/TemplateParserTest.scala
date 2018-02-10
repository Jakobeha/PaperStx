package paperstx.builder

import paperstx.utils.ResourceManager
import utest._

import scalaz.Success

object TemplateParserTest extends TestSuite {
  override def tests = Tests {
    def parseValidLang(filename: String) = {
      val templateString =
        ResourceManager.readTextFile(s"templates/$filename.pstx")
      val templateAST = TemplateParser.parse(templateString)
      println(templateAST)
      assertMatch(templateAST) {
        case Success(_) =>
      }
    }

    parseValidLang("Scheme")
    parseValidLang("Haskell")
  }
}
