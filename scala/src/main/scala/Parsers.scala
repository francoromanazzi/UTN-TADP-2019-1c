import scala.util.{Try}

package object Parsers {
  type Input = String

  type Parseado[T] = (T, Input)

  type Parser[T] = Input => Try[Parseado[T]]

  private val fstCharCondition: (Char => Boolean) => Parser[Char] = condition => input => Try {
    (input(0), input.substring(1)) match {
      case (c, resto) if condition(c) => (c, resto)
      case (c, _) => throw new Exception(s"Input started with $c")
    }
  }

  // TODO: mejorar el manejo de excepciones
  val anyChar: Parser[Char] = fstCharCondition(_ => true)

  // TODO: preguntar si no solo debería considerar el primer char
  val char: Char => Parser[Char] = char => fstCharCondition(_ == char)

  // TODO: preguntar si el string sin consumir debería ser todo el input en vez de sacarle el primer char
  val void: Parser[Unit] = input => Try {
    (Unit, anyChar(input).get._2)
  }

  // TODO: preguntar si no solo debería considerar el primer char
  val letter: Parser[Char] = fstCharCondition(_.isLetter)
}

