import Combinators._

import scala.util.{Success, Try}

package object Parsers {

  final case class ParserException(message: String = "") extends Exception(message)

  type Input = String
  type Parseado[+T] = (T, Input)
  type Parser[+T] = Input => Try[Parseado[T]]

  val success: Parser[Unit] = input => Success((Unit, input))

  val anyChar: Parser[Char] = input => Try(input(0), input.substring(1)).recover { case _ => throw new ParserException }

  val char: Char => Parser[Char] = char => anyChar.satisfies(_ == char)

  val void: Parser[Unit] = anyChar.const(Unit)

  val letter: Parser[Char] = anyChar.satisfies(_.isLetter)

  val digit: Parser[Char] = anyChar.satisfies(_.isDigit)

  val alphaNum: Parser[Char] = letter <|> digit

  // TODO foldear el string
  val string: String => Parser[String] = target => input => Try {
    var resultParsed: String = ""
    for ((c, i) <- target.zipWithIndex) {
      resultParsed += char(c)(input.substring(i)).get._1.toString
    }
    (resultParsed, input.substring(target.length))
  }
}

