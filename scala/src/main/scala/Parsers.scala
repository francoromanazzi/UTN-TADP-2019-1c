import scala.util.{Try}

package object Parsers {
  type Input = String

  type Parseado[T] = (T, Input)

  type Parser[T] = Input => Try[Parseado[T]]

  val anyChar: Parser[Char] = input => Try {
    (input(0), input.substring(1))
  }

  val char: Char => Parser[Char] = char => input => Try {
    anyChar(input).get match {
      case (c, resto) if c == char => (c, resto)
      case (c, _) => throw new Exception(s"Expected input to start with ${char} but got ${c}")
    }
  }
}

