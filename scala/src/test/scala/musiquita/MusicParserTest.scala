package musiquita

import Musica._
import Parsers.ParserException
import org.scalatest.{FreeSpec, Matchers}

class MusicParserTest extends FreeSpec with Matchers {
  def assertParsesSucceededWithResult[T](actualResult: T, expectedResult: T): Unit = {
    actualResult shouldBe expectedResult
  }

  def assertParseFailed[T](actualResult: ⇒ T): Unit = {
    assertThrows[ParserException](actualResult)
  }

  "musiquita.MusicParser" - {
  }
}
