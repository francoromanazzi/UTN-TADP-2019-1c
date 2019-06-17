import Parsers._

import scala.util.Try

package object Combinators {

  implicit class ParserExtendido[T](parser1: Parser[T]) {
    def <|>(parser2: Parser[T]): Parser[T] = input => Try {
      parser1(input).getOrElse(parser2(input).get)
    }

    def <>(parser2: Parser[T]): Parser[(T, T)] = input => Try {
      val (resultParser1, restoParser1) = parser1(input).get
      val (resultParser2, restoParser2) = parser2(restoParser1).get
      ((resultParser1, resultParser2), restoParser2)
    }
  }
}
