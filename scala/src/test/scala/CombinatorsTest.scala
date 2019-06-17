import Combinators._
import Parsers._
import Parsers.{Parseado, char}
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class CombinatorsTest extends FreeSpec with Matchers {
  def assertParsesSucceededWithResult[T](actualResult: Try[Parseado[T]], expectedResult: Parseado[T]): Unit = {
    actualResult.isSuccess shouldBe true
    actualResult.get shouldBe expectedResult
  }

  def assertParseFailed[T](actualResult: ⇒ Try[Parseado[T]]): Unit = {
    actualResult.isFailure shouldBe true
  }

  "Combinators" - {
    "<|> (OR combinator)" - {
      "when combining char('a') <|> char('b')" - {
        val aob: Parser[Char] = char('a') <|> char('b')

        "when fed an empty string" - {
          "it fails" in {
            assertParseFailed(aob(""))
          }
        }

        "when fed a string with one character" - {
          "if that string is not equal to any of the chars in the two parsers" - {
            "it fails" in {
              assertParseFailed(aob("c"))
            }
          }
          "if that string is equal to the char in the first parser" - {
            "it parses that char in the first parser" in {
              assertParsesSucceededWithResult(aob("a"), ('a', ""))
            }
          }
          "if that string is equal to the char in the second parser" - {
            "it parses that character" in {
              assertParsesSucceededWithResult(aob("b"), ('b', ""))
            }
          }
        }

        "when fed a string with more than one character" - {
          "if that string doesn't start with any of the chars in the two parsers" - {
            "it fails" in {
              assertParseFailed(aob("cab"))
            }
          }
          "if that string starts with the char in the first parser" - {
            "it parses that char in the first parser" in {
              assertParsesSucceededWithResult(aob("abab"), ('a', "bab"))
            }
          }
          "if that string starts with the char in the second parser" - {
            "it parses that char in the second parser" in {
              assertParsesSucceededWithResult(aob("bab"), ('b', "ab"))
            }
          }
        }
      }
    }
  }

}
