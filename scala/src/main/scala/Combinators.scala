import Parsers._

import scala.util.Success

package object Combinators {
  implicit class ParserExtendido[T](parser1: Parser[T]) {
    def <|>[R >: T, U <: R](parser2: Parser[U]): Parser[R] = input => parser1(input).recoverWith { case _ => parser2(input) }

    def <>[U](parser2: Parser[U]): Parser[(T, U)] = input => for {
      (resultParser1, restoParser1) <- parser1(input)
      (resultParser2, restoParser2) <- parser2(restoParser1)
    } yield ((resultParser1, resultParser2), restoParser2)

    def ~>[U](parser2: Parser[U]): Parser[U] = input => for {
      ((_, resultParser2), restoParser2) <- (parser1 <> parser2) (input)
    } yield (resultParser2, restoParser2)

    def <~[U](parser2: Parser[U]): Parser[T] = input => for {
      ((resultParser1, _), restoParser2) <- (parser1 <> parser2) (input)
    } yield (resultParser1, restoParser2)

    def sepBy[U](parserSeparador: Parser[U]): Parser[(T, T)] = (parser1 <~ parserSeparador) <> parser1

    def satisfies(condicion: T => Boolean): Parser[T] = parser1(_).filter { case (resultado, _) => condicion(resultado) }.recover { case _ => throw new ParserException }

    //def opt: Parser[Option[T]] = input => Success(parser1(input).fold(_ =>(None, input), {case (resultado, resto) => (Some(resultado), resto)}))
    def opt: Parser[Option[T]] = input => parser1.map(Some(_))(input).recover{ case _ => (None, input)}

    def * : Parser[List[T]] = input => Success(kleeneWithAccumulator((List(), input)))

    // @tailrec TODO no me deja anotarlo como tailrec, por qué?
    private def kleeneWithAccumulator(accum: Parseado[List[T]]): Parseado[List[T]] =
      parser1(accum._2).fold(
        _ => accum,
        { case (nuevoResultado, nuevoResto) => kleeneWithAccumulator((accum._1 :+ nuevoResultado, nuevoResto)) }
      )

    def + : Parser[List[T]] = parser1.*.satisfies(_.nonEmpty)

    def const[U](nuevoValor: U): Parser[U] = map(_ => nuevoValor)

    def map[U](transformacion: T => U): Parser[U] = input => for {
      (resultado, resto) <- parser1(input)
    } yield (transformacion(resultado), resto)
  }
}
