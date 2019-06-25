package musiquita

import Parsers._
import MusicParser._
import AudioPlayer._
import org.scalatest.{FreeSpec, Matchers}

class MusicParserTest extends FreeSpec with Matchers {
  def assertParsesSucceededWithResult[T](actualResult: T, expectedResult: T): Unit = {
    actualResult shouldBe expectedResult
  }

  def assertParseFailed[T](actualResult: ⇒ T): Unit = {
    assertThrows[ParserException](actualResult)
  }

  "MusicParser" - {
    "De musica ligera" in {
      val deMusicaLigera = melodia("_ 3Bm1/8 3Bm1/8 3Bm1/8 3Bm1/8 3GM1/8 3GM1/8 3GM1/8 3GM1/8 4DM1/8 4DM1/8 4DM1/8 4DM1/8 3AM1/8 3AM1/8 3AM1/8 3AM1/8" +
                                   " 3Bm1/8 3Bm1/8 3Bm1/8 3Bm1/8 3GM1/8 3GM1/8 3GM1/8 3GM1/8 4DM1/8 4DM1/8 4DM1/8 4DM1/8 3AM1/8 3AM1/8 3AM1/8 3AM1/8" +
                                   " 2B1/8 2B1/8 3C#1/8 3D1/1 ~ 3C#1/8 3D1/8 3C#1/8 2B1/8 2A1/8 2F#1/2 ~ 2B1/2 - -")
      deMusicaLigera.map { case (melodiaParseada, _) => reproducir(melodiaParseada) }
    }
  }
}
