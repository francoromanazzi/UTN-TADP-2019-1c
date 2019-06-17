import scala.util.{Try}

package object Parsers {
  type Input = String

  type Parseado[T] = (T, Input)

  type Parser[T] = Input => Try[Parseado[T]]

  // TODO: mejorar el manejo de excepciones
  val anyChar: Parser[Char] = input => Try {
    (input(0), input.substring(1))
  }

  // TODO: preguntar si no solo debería considerar el primer char
  val char: Char => Parser[Char] = char => input => Try {
    anyChar(input).get match {
      case (c, resto) if c == char => (c, resto)
      case (c, _) => throw new Exception(s"Expected input to start with ${char} but got ${c}")
    }
  }

  // TODO: preguntar si el string sin consumir debería ser todo el input en vez de sacarle el primer char
  val void: Parser[Unit] = input => Try {
    (Unit, anyChar(input).get._2)
  }

}

