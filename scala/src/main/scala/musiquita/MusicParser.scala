package musiquita

import Parsers._
import Combinators._
import musiquita.Musica._

package object MusicParser {

  val silencioBlanca: Parser[Silencio] = char('_').map(_ => Silencio(Blanca))
  val silencioNegra: Parser[Silencio] = char('-').map(_ => Silencio(Negra))
  val silencioCorchea: Parser[Silencio] = char('~').map(_ => Silencio(Corchea))
  val silencio: Parser[Silencio] = silencioBlanca <|> silencioNegra <|> silencioCorchea

  val octava: Parser[Int] = digit.map(_.asDigit)
  val nombreNota: Parser[Nota] = letter.map {
    case 'A' => A
    case 'B' => B
    case 'C' => C
    case 'D' => D
    case 'E' => E
    case 'F' => F
    case 'G' => G
  }.customException
  val modificadorNota: Parser[Nota => Nota] = (char('#') <|> char('b')).opt.map{
    case Some('#') => nota => nota.sostenido
    case Some('b') => nota => nota.bemol
    case None => nota => nota
  }
  val nota: Parser[Nota] = (nombreNota <> modificadorNota).map{ case (_nota, _modificadorNota) => _modificadorNota(_nota)}
  val tono: Parser[Tono] = (octava <> nota).map{ case (_octava, _nota) => Tono(_octava, _nota)}
  val figura: Parser[Figura] = integer.sepBy(char('/')).map {
    case (1, 1) => Redonda
    case (1, 2) => Blanca
    case (1, 4) => Negra
    case (1, 8) => Corchea
    case (1, 16) => SemiCorchea
  }.customException
  val sonido: Parser[Sonido] = (tono <> figura).map{ case (_tono, _figura) => Sonido(_tono, _figura)}

  //val acorde: Parser[Acorde] = ???

  val tocable: Parser[Tocable] = silencio <|> sonido //<|> acorde

  val melodia: Parser[Melodia] = (tocable <> (char(' ') ~> tocable).*).opt.map {
    case Some((primerTocable, listaTocables)) => primerTocable :: listaTocables
    case None => List()
  }
}