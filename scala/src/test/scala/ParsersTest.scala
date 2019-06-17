import Parsers._
import org.scalatest.{FreeSpec, Matchers}

import scala.util.{Try}

class ParsersTest extends FreeSpec with Matchers {
  def assertParsesSucceededWithResult[T](actualResult: Try[Parseado[T]], expectedResult: Parseado[T]): Unit = {
    actualResult.isSuccess shouldBe true
    actualResult.get shouldBe expectedResult
  }

  def assertParseFailed[T](actualResult: ⇒ Try[Parseado[T]]): Unit = {
    actualResult.isFailure shouldBe true
  }

  "Parsers" - {
    "anyChar" - {
      "when fed an empty string" - {
        "it fails" in {
          assertParseFailed(anyChar(""))
        }
      }

      "when fed a string with one character" - {
        "it parses that character" in {
          assertParsesSucceededWithResult(anyChar("H"), ('H', ""))
        }
      }

      "when fed a string with more than one character" - {
        "it parses the first character" in {
          assertParsesSucceededWithResult(anyChar("Hola"), ('H', "ola"))
        }
      }
    }

    "char" - {
      "when fed an empty string" - {
        "it fails" in {
          assertParseFailed(char('H')(""))
        }
      }

      "when fed a string with one character" - {
        "if that string is not equal to that character" - {
          "it fails" in {
            assertParseFailed(char('H')("C"))
          }
        }
        "if that string is equal to that character" - {
          "it parses that character" in {
            assertParsesSucceededWithResult(char('H')("H"), ('H', ""))
          }
        }
      }

      "when fed a string with more than one character" - {
        "if that string doesn't start with that that character" - {
          "it fails" in {
            assertParseFailed(char('H')("Chau"))
          }
        }
        "if that string starts with that character" - {
          "it parses that character" in {
            assertParsesSucceededWithResult(char('H')("Hola"), ('H', "ola"))
          }
        }
      }
    }

    "void" - {
      "when fed an empty string" - {
        "it fails" in {
          assertParseFailed(void(""))
        }
      }

      "when fed a string with one character" - {
        "it parses that character but discards it" in {
          assertParsesSucceededWithResult(void("H"), ((), ""))
        }
      }

      "when fed a string with more than one character" - {
        "it parses the first character but discards it" in {
          assertParsesSucceededWithResult(void("Hola"), ((), "ola"))
        }
      }
    }
  }
}
