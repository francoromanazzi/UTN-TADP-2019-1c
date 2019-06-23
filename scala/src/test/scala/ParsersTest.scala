import Parsers._
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class ParsersTest extends FreeSpec with Matchers {
  def assertParsesSucceededWithResult[T](actualResult: Try[Parseado[T]], expectedResult: Parseado[T]): Unit = {
    actualResult.get shouldBe expectedResult
  }

  def assertParseFailed[T](actualResult: ⇒ Try[Parseado[T]]): Unit = {
    intercept[ParserException] { actualResult.get }
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
        "if that string doesn't start with that character" - {
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

    "letter" - {
      "when fed an empty string" - {
        "it fails" in {
          assertParseFailed(letter(""))
        }
      }

      "when fed a string with one character" - {
        "if that string is not a letter" - {
          "it fails" in {
            assertParseFailed(letter("1"))
          }
        }
        "if that string is a letter" - {
          "if it is uppercase" - {
            "it parses that letter" in {
              assertParsesSucceededWithResult(letter("H"), ('H', ""))
            }
          }
          "if it is lowercase" - {
            "it parses that letter" in {
              assertParsesSucceededWithResult(letter("h"), ('h', ""))
            }
          }
        }
      }

      "when fed a string with more than one character" - {
        "if that string doesn't start with a letter" - {
          "it fails" in {
            assertParseFailed(letter("1Hola"))
          }
        }
        "if that string starts with a letter" - {
          "it parses that letter" in {
            assertParsesSucceededWithResult(letter("Hola"), ('H', "ola"))
          }
        }
      }
    }

    "digit" - {
      "when fed an empty string" - {
        "it fails" in {
          assertParseFailed(digit(""))
        }
      }

      "when fed a string with one character" - {
        "if that string is not a digit" - {
          "it fails" in {
            assertParseFailed(digit("A"))
          }
        }
        "if that string is a digit" - {
          "it parses that digit" in {
            assertParsesSucceededWithResult(digit("0"), ('0', ""))
          }
        }
      }

      "when fed a string with more than one character" - {
        "if that string doesn't start with a digit" - {
          "it fails" in {
            assertParseFailed(digit("Hola1"))
          }
        }
        "if that string starts with a digit" - {
          "it parses that digit" in {
            assertParsesSucceededWithResult(digit("1Hola"), ('1', "Hola"))
          }
        }
      }
    }

    "alphaNum" - {
      "when fed an empty string" - {
        "it fails" in {
          assertParseFailed(alphaNum(""))
        }
      }

      "when fed a string with one character" - {
        "if that string is not alphanum" - {
          "it fails" in {
            assertParseFailed(alphaNum("#"))
          }
        }
        "if that string is alphanum" - {
          "if it is a digit" - {
            "it parses that digit" in {
              assertParsesSucceededWithResult(alphaNum("0"), ('0', ""))
            }
          }
          "if it is a letter" - {
            "it parses that letter" in {
              assertParsesSucceededWithResult(alphaNum("a"), ('a', ""))
            }
          }
        }
      }

      "when fed a string with more than one character" - {
        "if that string doesn't start with alphanum" - {
          "it fails" in {
            assertParseFailed(alphaNum("#Hola1"))
          }
        }
        "if that string starts with alphanum" - {
          "if it is a digit" - {
            "it parses that digit" in {
              assertParsesSucceededWithResult(alphaNum("0Hola"), ('0', "Hola"))
            }
          }
          "if it is a letter" - {
            "it parses that letter" in {
              assertParsesSucceededWithResult(alphaNum("aHola"), ('a', "Hola"))
            }
          }
        }
      }
    }

    "string" - {
      "when fed an empty string" - {
        "if the matcher is not an empty string" - {
          "it fails" in {
            assertParseFailed(string("Hola")(""))
          }
        }
        "if the matcher is an empty string" - {
          "it parses the empty string" in {
            assertParsesSucceededWithResult(string("")(""), ("", ""))
          }
        }
      }

      "when fed a string with one character" - {
        "if that string is not equal to that character" - {
          "it fails" in {
            assertParseFailed(string("Hola")("H"))
          }
        }
        "if that string is equal to that character" - {
          "it parses that character" in {
            assertParsesSucceededWithResult(string("H")("H"), ("H", ""))
          }
        }
        "if the matcher is an empty string" - {
          "it parses the empty string" in {
            assertParsesSucceededWithResult(string("")("H"), ("", "H"))
          }
        }
      }

      "when fed a string with more than one character" - {
        "if that string doesn't start with the string matcher" - {
          "it fails" in {
            assertParseFailed(string("Hola")("Holgado"))
          }
        }
        "if that string starts with the string matcher" - {
          "it parses that string" in {
            assertParsesSucceededWithResult(string("Hola")("HolaMundo!"), ("Hola", "Mundo!"))
          }
        }
        "if the matcher is an empty string" - {
          "it parses the empty string" in {
            assertParsesSucceededWithResult(string("")("HolaMundo!"), ("", "HolaMundo!"))
          }
        }
      }
    }

    "integer" - {
      "when fed an empty string" - {
        "it parses the empty string" in {
          assertParseFailed(integer(""))
        }
      }

      "when fed a string with one character" - {
        "if that string is not a digit" - {
          "it fails" in {
            assertParseFailed(integer("H"))
          }
        }
        "if that string is a digit" - {
          "it parses that int" in {
            assertParsesSucceededWithResult(integer("1"), (1, ""))
          }
        }
      }

      "when fed a string with more than one character" - {
        "if that string doesn't start with a digit" - {
          "it fails" in {
            assertParseFailed(integer("H1123"))
          }
        }
        "if that string starts with a digit" - {
          "it parses that int" in {
            assertParsesSucceededWithResult(integer("1Hola"), (1, "Hola"))
          }
        }
        "if that string starts with multiple digits" - {
          "it parses that int" in {
            assertParsesSucceededWithResult(integer("123Hola"), (123, "Hola"))
          }
        }
      }
    }
  }
}
