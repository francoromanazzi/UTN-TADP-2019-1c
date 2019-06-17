import Parsers._

import scala.util.Try

package object Combinators {

  implicit class ParserExtendido[T](parser1: Parser[T]) {
    def <|> (parser2: Parser[T]): Parser[T] = input => Try {
      parser1(input).getOrElse(parser2(input).get)
    }
  }
}
