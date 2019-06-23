import Parsers._

import scala.util.Success

package object Combinators {

  implicit class ParserExtendido[T](parser1: Parser[T]) {

    def <|>[R >: T, U <: R](parser2: Parser[U]): Parser[R] = input => parser1(input).recoverWith { case _ => parser2(input) }

    def <>[U](parser2: Parser[U]): Parser[(T, U)] = input => for {
      (resultParser1, restoParser1) <- parser1(input)
      (resultParser2, restoParser2) <- parser2(restoParser1)
    } yield ((resultParser1, resultParser2), restoParser2)

    // TODO testearlo
    def ~>[U](parser2: Parser[U]): Parser[U] = input => for {
      ((_, resultParser2), restoParser2) <- (parser1 <> parser2) (input)
    } yield (resultParser2, restoParser2)

    // TODO testearlo
    def <~[U](parser2: Parser[U]): Parser[T] = input => for {
      ((resultParser1, _), restoParser2) <- (parser1 <> parser2) (input)
    } yield (resultParser1, restoParser2)

    // TODO testearlo
    def sepBy(parserSeparador: Parser[T]): Parser[T] = input => ???

    // TODO testearlo
    def satisfies(condicion: T => Boolean): Parser[T] = parser1(_).filter { case (resultado, _) => condicion(resultado) }.recover { case _ => throw new ParserException }

    // TODO testearlo
    def opt: Parser[Any] = parser1 <|> success

    // TODO testearlo
    def * : Parser[List[T]] = input => Success(kleeneWithAccumulator((List(), input)))

    // @tailrec TODO no me deja anotarlo como tailrec, por qué?
    private def kleeneWithAccumulator(accum: Parseado[List[T]]): Parseado[List[T]] = {
      parser1(accum._2).fold(
        _ => accum,
        { case (nuevoResultado, nuevoResto) => kleeneWithAccumulator((nuevoResultado :: accum._1, nuevoResto)) }
      )
    }

    // TODO testearlo
    def + : Parser[List[T]] = parser1.*.satisfies(_.nonEmpty)

    // TODO testearlo
    def const[U](nuevoValor: U): Parser[U] = map(_ => nuevoValor)

    // TODO testearlo
    def map[U](transformacion: T => U): Parser[U] = input => for {
      (resultado, resto) <- parser1(input)
    } yield (transformacion(resultado), resto)
  }

}
