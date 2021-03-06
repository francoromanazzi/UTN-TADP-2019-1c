import Combinators._

import scala.util.{Success, Try}

package object Parsers {

  final case class ParserException(message: String = "") extends Exception(message)

  type Input = String
  type Parseado[+T] = (T, Input)
  type Parser[+T] = Input => Try[Parseado[T]]

  val anyChar: Parser[Char] = {
    val ret: Parser[Char] = input => Try(input(0), input.substring(1))
    ret.customException
  }

  val char: Char => Parser[Char] = char => anyChar.satisfies(_ == char)

  val void: Parser[Unit] = anyChar.const(Unit)

  val letter: Parser[Char] = anyChar.satisfies(_.isLetter)

  val digit: Parser[Char] = anyChar.satisfies(_.isDigit)

  val alphaNum: Parser[Char] = letter <|> digit

  private def successWithResult[T](value: T): Parser[T] = input => Success((value, input))
  val string: String => Parser[String] = _.toList.map(char(_)).foldLeft(successWithResult("")) { (parserAccum: Parser[String], charParser) =>
    (parserAccum <> charParser)
      .map { case (strAccum, charNuevo) => strAccum + charNuevo.toString }
  }

  val integer: Parser[Int] = digit.+.map(_.mkString.toInt)
}

