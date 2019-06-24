package musiquita

import Parsers._
import Combinators._
import musiquita.Musica._

package object MusicParser {

  val silencioBlanca: Parser[Silencio] = char('_').map(_ => Silencio(Blanca))
  val silencioNegra: Parser[Silencio] = char('-').map(_ => Silencio(Negra))
  val silencioCorchea: Parser[Silencio] = char('~').map(_ => Silencio(Corchea))
  val silencio: Parser[Silencio] = silencioBlanca <|> silencioNegra <|> silencioCorchea

  val sonido: Parser[Sonido] = ???

  val acorde: Parser[Acorde] = ???

  val tocable: Parser[Tocable] = silencio <|> sonido <|> acorde

  val melodia: Parser[Melodia] = ((tocable <~ char(' ')).* <> tocable.opt).map {
    case (listaTocables, Some(ultimoTocable)) => listaTocables :+ ultimoTocable
    case (listaTocables, None) => listaTocables
  }
}