package paperstx.parser

import paperstx.utils.ResourceManager
import utest._
import fastparse.all._

object TemplateParserTest extends TestSuite {
  override def tests = Tests {
    def parseValidLang(filename: String) = {
      val templateString =
        ResourceManager.readTextFile(s"templates/$filename.pstx")
      val templateAST = TemplateParser.templateFile.parse(templateString)
      println(templateAST)
      assertMatch(templateAST) {
        case Parsed.Success(_, _) =>
      }
    }

    parseValidLang("Scheme")
    parseValidLang("Haskell")
  }
}
