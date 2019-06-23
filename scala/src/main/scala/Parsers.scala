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

  // TODO ver como sacar esta funcion auxiliar y usar el success como semilla del fold
  private val successString: Parser[String] = input => Success(("", input))
  val string: String => Parser[String] = _.toList.map(char(_)).foldLeft(successString) { (parserAccum: Parser[String], charParser) =>
    (parserAccum <> charParser)
      .map { case (strAccum, charNuevo) => strAccum + charNuevo.toString }
  }
}

