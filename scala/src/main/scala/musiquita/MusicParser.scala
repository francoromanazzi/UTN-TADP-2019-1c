package musiquita

import Parsers._
import Combinators._
import musiquita.Musica._

package object MusicParser {

  val silencio: Parser[Tocable] = ???

  val sonido: Parser[Tocable] = ???

  val acorde: Parser[Tocable] = ???

  val tocable: Parser[Tocable] = silencio <|> sonido <|> acorde

  val melodia: Parser[Melodia] = ((tocable <~ char(' ')).* <> tocable.opt).map {
    case (listaTocables, Some(ultimoTocable)) => listaTocables :+ ultimoTocable
    case (listaTocables, None) => listaTocables
  }
}