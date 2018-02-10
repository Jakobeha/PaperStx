package paperstx.parser

import paperstx.utils.ResourceManager
import utest._
import fastparse.all._

object TemplateParserTest extends TestSuite {
  override def tests = Tests {
    val templateString = ResourceManager.readTextFile("templates/Scheme.pstx")
    val templateAST = TemplateParser.templateFile.parse(templateString)
    println(templateAST)
    assertMatch(templateAST) {
      case Parsed.Success(_, _) =>
    }
  }
}
