package musiquita

import Parsers._
import Combinators._
import musiquita.Musica._

package object MusicParser {

  val silencio: Parser[Silencio] = ???

  val sonido: Parser[Sonido] = ???

  val acorde: Parser[Acorde] = ???

  val tocable: Parser[Tocable] = silencio <|> sonido <|> acorde

  val melodia: Parser[Melodia] = ((tocable <~ char(' ')).* <> tocable.opt).map {
    case (listaTocables, Some(ultimoTocable)) => listaTocables :+ ultimoTocable
    case (listaTocables, None) => listaTocables
  }
}