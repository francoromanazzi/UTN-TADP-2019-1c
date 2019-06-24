package musiquita

import Parsers.ParserException
import MusicParser._
import org.scalatest.{FreeSpec, Matchers}

class MusicParserTest extends FreeSpec with Matchers {
  def assertParsesSucceededWithResult[T](actualResult: T, expectedResult: T): Unit = {
    actualResult shouldBe expectedResult
  }

  def assertParseFailed[T](actualResult: ⇒ T): Unit = {
    assertThrows[ParserException](actualResult)
  }

  "MusicParser" - {
    "_______________________" in {
      println(tocable(""))
      println(tocable("_"))
      println(tocable("4C1/4"))
      println(tocable("4C1/4 "))
      println(tocable("4C1/4 4C1/4"))
    }
  }
}
