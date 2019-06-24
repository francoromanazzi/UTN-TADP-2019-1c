import Combinators._
import Parsers.{Parseado, char, _}
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class CombinatorsTest extends FreeSpec with Matchers {
  def assertParsesSucceededWithResult[T](actualResult: Try[Parseado[T]], expectedResult: Parseado[T]): Unit = {
    actualResult.get shouldBe expectedResult
  }

  def assertParseFailed[T](actualResult: ⇒ Try[Parseado[T]]): Unit = {
    intercept[ParserException] {
      actualResult.get
    }
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
      "when combining string(\"ab\") <|> char('a')" - {
        val abOrA: Parser[Any] = string("ab") <|> char('a')

        "when fed an empty string" - {
          "it fails" in {
            assertParseFailed(abOrA(""))
          }
        }

        "when fed a string with one character" - {
          "if that string is not equal to the char in the 2nd parser" - {
            "it fails" in {
              assertParseFailed(abOrA("c"))
            }
          }
          "if that string is equal to the char in the second parser" - {
            "it parses that character" in {
              assertParsesSucceededWithResult(abOrA("a"), ('a', ""))
            }
          }
        }

        "when fed a string with more than one character" - {
          "if that string doesn't match any of the parsers" - {
            "it fails" in {
              assertParseFailed(abOrA("cab"))
            }
          }
          "if that string starts with the string in the 1st parser" - {
            "it parses that char in the first parser" in {
              assertParsesSucceededWithResult(abOrA("abab"), ("ab", "ab"))
            }
          }
          "if that string starts with the char in the 2nd parser" - {
            "it parses that char in the second parser" in {
              assertParsesSucceededWithResult(abOrA("aab"), ('a', "ab"))
            }
          }
        }
      }
    }

    "<> (Concat combinator)" - {
      "when combining string(\"hola\") <> string(\"mundo\")" - {
        val holamundo: Parser[(String, String)] = string("hola") <> string("mundo")

        "when fed an empty string" - {
          "it fails" in {
            assertParseFailed(holamundo(""))
          }
        }

        "when fed a string with more than one character" - {
          "if that string doesn't start with both the strings in the two parsers" - {
            "it fails" in {
              assertParseFailed(holamundo("holachau"))
            }
          }
          "if that string starts with both the strings in the two parsers" - {
            "it parses the two strings" in {
              assertParsesSucceededWithResult(holamundo("holamundo!"), (("hola", "mundo"), "!"))
            }
          }
        }
      }
      "when combining digit <> string(\"mundo\")" - {
        val digitMundo: Parser[(Char, String)] = digit <> string("mundo")

        "when fed an empty string" - {
          "it fails" in {
            assertParseFailed(digitMundo(""))
          }
        }

        "when fed a string with more than one character" - {
          "if that string doesn't match the two parsers" - {
            "it fails" in {
              assertParseFailed(digitMundo("12mundo!"))
            }
          }
          "if that string matches the two parsers" - {
            "it parses the two strings" in {
              assertParsesSucceededWithResult(digitMundo("1mundo!"), (('1', "mundo"), "!"))
            }
          }
        }
      }
    }
  }

  "Operations" - {
    "sepBy" - {
      "when parsing a telephone number like 4501-2251" - {
        val telNum: Parser[(Int, Int)] = integer.sepBy(char('-'))

        "when fed an empty string" - {
          "it fails" in {
            assertParseFailed(telNum(""))
          }
        }

        "when fed a string with one character" - {
          "if that string is a digit" - {
            "it fails" in {
              assertParseFailed(telNum("1"))
            }
          }
          "if that string is a hyphen" - {
            "it fails" in {
              assertParseFailed(telNum("-"))
            }
          }
        }

        "when fed a string with more than one character" - {
          "if that string is only digits" - {
            "it fails" in {
              assertParseFailed(telNum("4501"))
            }
          }
          "if that string is digits separated by a space" - {
            "it fails" in {
              assertParseFailed(telNum("4501 2251"))
            }
          }
          "if that string is digits separated by a hyphen" - {
            "it parses that telephone number" in {
              assertParsesSucceededWithResult(telNum("4501-2251hola"), ((4501, 2251), "hola"))
            }
          }
        }
      }
    }

    "opt" - {
      "when making optional string(\"in\")" - {
        val talVezIn: Parser[Any] = string("in").opt

        "when fed an empty string" - {
          "it succedes, parsing nothing and returning the same input" in {
            assertParsesSucceededWithResult(talVezIn(""), ((), ""))
          }
        }

        "when fed a string with more than one character" - {
          "if the string starts with that matcher" in {
            assertParsesSucceededWithResult(talVezIn("infija"), ("in", "fija"))
          }
          "if the string doesn't start with that matcher" in {
            assertParsesSucceededWithResult(talVezIn("fija"), ((), "fija"))
          }
        }
      }
      "when making optional string(\"in\") and concatenating it with string(\"fija\")" - {
        val talVezIn: Parser[Any] = string("in").opt
        val precedencia: Parser[(Any, String)] = talVezIn <> string("fija")


        "when fed an empty string" - {
          "it succedes, parsing nothing and returning the same input" in {
            assertParseFailed(precedencia(""))
          }
        }

        "when fed a string with more than one character" - {
          "if the string starts with that matcher" in {
            assertParsesSucceededWithResult(precedencia("infija"), (("in", "fija"), ""))
          }
          "if the string doesn't start with that matcher" in {
            assertParsesSucceededWithResult(precedencia("fija"), (((), "fija"), ""))
          }
        }
      }
    }

    "*" - {
      "when applying * to digit" - {
        val zeroOrMoreDigits: Parser[List[Char]] = digit.*

        "when fed an empty string" - {
          "it parses an empty list" in {
            assertParsesSucceededWithResult(zeroOrMoreDigits(""), (List(), ""))
          }
        }

        "when fed a string with more than one character" - {
          "if it doesn't start with a digit" - {
            "it parses an empty list" in {
              assertParsesSucceededWithResult(zeroOrMoreDigits("a123"), (List(), "a123"))
            }
          }
          "if it starts with one digit" - {
            "it parses the single digit" in {
              assertParsesSucceededWithResult(zeroOrMoreDigits("1a"), (List('1'), "a"))
            }
          }
          "if it starts with more than one digit" - {
            "it parses the digits" in {
              assertParsesSucceededWithResult(zeroOrMoreDigits("123a"), (List('1', '2', '3'), "a"))
            }
          }
        }
      }
    }

    "+" - {
      "when applying + to digit" - {
        val oneOrMoreDigits: Parser[List[Char]] = digit.+

        "when fed an empty string" - {
          "it fails" in {
            assertParseFailed(oneOrMoreDigits(""))
          }
        }

        "when fed a string with more than one character" - {
          "if it doesn't start with a digit" - {
            "it fails" in {
              assertParseFailed(oneOrMoreDigits("a123"))
            }
          }
          "if it starts with one digit" - {
            "it parses the single digit" in {
              assertParsesSucceededWithResult(oneOrMoreDigits("1a"), (List('1'), "a"))
            }
          }
          "if it starts with more than one digit" - {
            "it parses the digits" in {
              assertParsesSucceededWithResult(oneOrMoreDigits("123a"), (List('1', '2', '3'), "a"))
            }
          }
        }
      }
    }
  }
}
