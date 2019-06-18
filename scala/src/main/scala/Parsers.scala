import Combinators._

import scala.util.{Success, Try}

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

  val char: Char => Parser[Char] = char => fstCharCondition(_ == char)

  // TODO: preguntar si el string sin consumir debería ser todo el input en vez de sacarle el primer char
  val void: Parser[Unit] = input => Try {
    (Unit, anyChar(input).get._2)
  }

  val letter: Parser[Char] = fstCharCondition(_.isLetter)

  val digit: Parser[Char] = fstCharCondition(_.isDigit)

  val alphaNum: Parser[Char] = letter <|> digit

  val string: String => Parser[String] = target => input => Try {
    var resultParsed: String = ""
    for((c, i) <- target.zipWithIndex) {
      resultParsed += char(c)(input.substring(i)).get._1.toString
    }
    (resultParsed, input.substring(target.length))
  }
}

