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
  }
}
