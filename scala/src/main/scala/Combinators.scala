import Parsers._

import scala.util.{Success, Try}

package object Combinators {

  implicit class ParserExtendido[T](parser1: Parser[T]) {

    sealed trait Nullable
    case class SomeValue(value: T) extends Nullable
    case class NullValue(value: Unit) extends Nullable

    def <|>(parser2: Parser[T]): Parser[T] = input => Try {
      parser1(input).getOrElse(parser2(input).get)
    }

    def <>[U](parser2: Parser[U]): Parser[(T, U)] = input => Try {
      val (resultParser1, restoParser1) = parser1(input).get
      val (resultParser2, restoParser2) = parser2(restoParser1).get
      ((resultParser1, resultParser2), restoParser2)
    }

    // TODO testearlo
    def ~>[U](parser2: Parser[U]): Parser[U] = input => Try {
      val ((_, resultParser2), restoParser2) = (parser1 <> parser2)(input).get
      (resultParser2, restoParser2)
    }

    // TODO testearlo
    def <~[U](parser2: Parser[U]): Parser[T] = input => Try {
      val ((resultParser1, _), restoParser2) = (parser1 <> parser2)(input).get
      (resultParser1, restoParser2)
    }

    // TODO testearlo
    def sepBy(parserSeparador: Parser[T]): Parser[T] = input => Try {
      ???
    }

    // TODO testearlo
    def satisfies(condicion: T => Boolean): Parser[T] = input => Try {
      parser1(input).get match {
        case (resultado, resto) if condicion(resultado) => (resultado, resto)
        case _ => throw new Exception("No se cumplio la condicion")
      }
    }

    // TODO testearlo
    def opt: Parser[T] = ???

    // TODO testearlo
    def * : Parser[List[T]] = input => ???

    // TODO testearlo
    def + : Parser[List[T]] = input => ???

    // TODO testearlo
    def const[U](nuevoValor: U): Parser[U] = map(_ => nuevoValor)

    // TODO testearlo
    def map[U](transformacion: T => U): Parser[U] = input => Try {
      val (resultado, resto) = parser1(input).get
      (transformacion(resultado), resto)
    }
  }
}
