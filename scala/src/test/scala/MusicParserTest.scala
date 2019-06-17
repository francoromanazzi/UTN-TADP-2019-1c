import Musica._
import org.scalatest.{FreeSpec, Matchers}

class MusicParserTest extends FreeSpec with Matchers {
  def assertParsesSucceededWithResult[T](actualResult: T, expectedResult: T): Unit = {
    actualResult shouldBe expectedResult
  }

  def assertParseFailed[T](actualResult: ⇒ T): Unit = {
    assertThrows[ParserException](actualResult)
  }

  "MusicParser" - {
    "when fed empty text" - {
      "parses an empty list of notes" in {
        assertParsesSucceededWithResult(new MusicParser("").parse(), Nil)
      }
    }

    "when fed a text that is a note letter" - {
      "parses a list with that one note" in {
        assertParsesSucceededWithResult(new MusicParser("A").parse(), List(A))
      }
    }

    "when it is fed a text that ends in a space" - {
      "it parses it as it that space wasn't there" in {
        assertParsesSucceededWithResult(new MusicParser("A ").parse(), List(A))
      }
    }

    "when fed a text that is a non valid letter for a note" - {
      "it fails" in {
        assertParseFailed(new MusicParser("J").parse(), List(A))
      }
    }

    "when fed a text that is a note letter followed by something that is not a note" - {
      "it fails" in {
        assertParseFailed(new MusicParser("A J").parse(), List(A))
      }
    }

    "when it is fed a text that is several notes in a row" - {
      "parses a list with the different notes in order" in {
        assertParsesSucceededWithResult(new MusicParser("AB").parse(), List(A, B))
      }

      "even when the notes are separated by a space" - {
        "parses a list with the different notes in order" in {
          assertParsesSucceededWithResult(new MusicParser("A B").parse(), List(A, B))
        }
      }

      "even when the notes are separated by several spaces" - {
        "parses a list with the different notes in order" in {
          assertParsesSucceededWithResult(new MusicParser("A  B").parse(), List(A, B))
        }
      }
    }

    "when fed a text that is a single note repeated multiple times times" - {
      "it parses a list with that note appearing two times" in {
        assertParsesSucceededWithResult(new MusicParser("2x(A)").parse(), List(A, A))
      }

      "it parses a list with that note appearing three times" in {
        assertParsesSucceededWithResult(new MusicParser("3x(A)").parse(), List(A, A, A))
      }
    }

    "when fed a text that contains a wrong note inside a repetition" - {
      "it fails" in {
        assertParseFailed(new MusicParser("3x(A J)").parse(), List(A))
      }
    }

    "when fed a text that is multiple notes repeated multiple times" - {
      "it parses a list with those two notes appearing two times" in {
        assertParsesSucceededWithResult(new MusicParser("2x(A B)").parse(), List(A, B, A, B))
      }

      "it parses a list with those two notes appearing three times" in {
        assertParsesSucceededWithResult(new MusicParser("3x(A B)").parse(), List(A, B, A, B, A, B))
      }
    }

    "when fed a text contains multiple notes repeated multiple times along with regular notes" - {
      "it parses a list with those two notes appearing two times after a regular note" in {
        assertParsesSucceededWithResult(new MusicParser("C 2x(A B)").parse(), List(C, A, B, A, B))
      }

      "it parses a list with those two notes appearing two times before a regular note" in {
        assertParsesSucceededWithResult(new MusicParser("2x(A B) C").parse(), List(A, B, A, B, C))
      }
    }

    "when fed a text that contains repetitions inside another repetition" - {
      "it parses a list with all nested repetitions resolved into regular notes" in {
        assertParsesSucceededWithResult(new MusicParser("2x(A B 3x(F G 2x(A))) F B E").parse(),
          List(A, B, F, G, A, A, F, G, A, A, F, G, A, A, A, B, F, G, A, A, F, G, A, A, F, G, A, A, F, B, E))
      }
    }

    "when fed a text that contains a two digit repetition" - {
      "it parses a list with that note apearing twelve times" in {
        assertParsesSucceededWithResult(new MusicParser("12x(A)").parse(),
          List(A, A, A, A, A, A, A, A, A, A, A, A))
      }
    }
  }
}
