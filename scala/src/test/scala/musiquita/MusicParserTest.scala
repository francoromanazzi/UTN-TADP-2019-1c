package musiquita

import Parsers._
import MusicParser._
import Combinators._
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
      println(melodia(""))
      println(melodia("_"))
      println(melodia("4C1/4"))
      println(melodia("4C1/4 "))
      println(melodia("4C1/4 4C1/4"))
      println(melodia("4C1/4 4C1/4 "))
    }
  }
}
