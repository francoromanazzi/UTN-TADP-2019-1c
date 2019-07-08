package musiquita

import Combinators._
import Parsers._
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
  val modificadorNota: Parser[Nota => Nota] = (char('#') <|> char('b'))
    .optMap(
      nota => nota, {
        case '#' => nota => nota.sostenido
        case 'b' => nota => nota.bemol
      })

  val nota: Parser[Nota] = (nombreNota <> modificadorNota).map { case (unaNota, unModificadorNota) => unModificadorNota(unaNota) }
  val tono: Parser[Tono] = (octava <> nota).map { case (unaOctava, unaNota) => Tono(unaOctava, unaNota) }
  val figura: Parser[Figura] = integer.sepBy(char('/')).map {
    case (1, 1) => Redonda
    case (1, 2) => Blanca
    case (1, 4) => Negra
    case (1, 8) => Corchea
    case (1, 16) => SemiCorchea
  }.customException
  val sonido: Parser[Sonido] = (tono <> figura).map { case (unTono, unaFigura) => Sonido(unTono, unaFigura) }

  val acordeExplicito: Parser[Acorde] = ((tono <> (char('+') ~> tono).+) <> figura)
    .map {
      case ((unTono, unosTonos), unaFigura) => Acorde(unTono :: unosTonos, unaFigura)
    }
  val acordeMenorOMayor: Parser[Acorde] = (tono <> (char('m') <|> char('M')) <> figura)
    .map {
      case ((unTono, 'M'), unaFigura) => unTono.nota.acordeMayor(unTono.octava, unaFigura)
      case ((unTono, 'm'), unaFigura) => unTono.nota.acordeMenor(unTono.octava, unaFigura)
    }
  val acorde: Parser[Acorde] = acordeExplicito <|> acordeMenorOMayor

  val tocable: Parser[Tocable] = silencio <|> sonido <|> acorde

  val melodia: Parser[Melodia] = (tocable <> (char(' ') ~> tocable).*)
    .optMap(List(), { case (primerTocable, listaTocables) => primerTocable :: listaTocables })
}
