import java.io.{PushbackReader, StringReader}
import Musica._
import scala.collection.mutable.ListBuffer

// case class Note(name: String)

class MusicParser(input: String) {
  protected var inputStream = new PushbackReader(new StringReader(input))

  protected def parseChar(): Char = {
    val parsed = inputStream.read()
    if (parsed == -1) throw new EOIParserException
    parsed.toChar
  }

  protected def parseNote(): Nota = {
    var next: Char = ' '
    do next = parseChar() while (next == ' ')
    Nota.notas.find(_.toString == next.toString()).getOrElse(throw new NotANoteException(next))
  }

  protected def resolveRepetitions() : Boolean = {
    var result: String = ""
    var next: Char = ' '
    var hasFlattenedRepetition: Boolean = false

    try while (true) {
      do next = parseChar() while (next == ' ')

      // Multiplicador
      if(next.isDigit) {
        var multiplicador: Int = next.asDigit
        do {
          next = parseChar()

          if(next.isDigit) multiplicador = multiplicador * 10 + next.asDigit
          else if(next != 'x') throw new RepetitionSyntaxException

        } while(next != 'x')

        next = parseChar()
        if(next != '(') throw new RepetitionSyntaxException

        var stringAMultiplicar: String = ""
        do {
          next = parseChar()
          if(next != ')') stringAMultiplicar += next
        } while(next != ')')

        result += stringAMultiplicar * multiplicador
        println(stringAMultiplicar, multiplicador)
        hasFlattenedRepetition = true
      }
      else {
        result += next
      }
    }
    catch {
      case _: EOIParserException =>
    }

    println(result, result.size)
    inputStream = new PushbackReader(new StringReader(result))

    hasFlattenedRepetition
  }

  def parse(): List[Nota] = {
    while(resolveRepetitions()) {}

    val result: ListBuffer[Nota] = ListBuffer()
    try while (true)
      result += parseNote()
    catch {
      case _: EOIParserException =>
    }
    result.toList
  }
}

class ParserException(reason: String) extends Exception(reason)
class EOIParserException extends ParserException("reached end of input")
class NotANoteException(val read: Char) extends ParserException(s"Expected [A|B|C|D|E|F|G] but got $read")
class RepetitionSyntaxException extends ParserException("Error en la sintaxis del multiplicador")