import scala.util.{Try}

package object Parsers {
  type Input = String

  type Parseado[T] = (T, Input)

  type Parser[T] = Input => Try[Parseado[T]]

  val anyChar: Parser[Char] = str => Try{(str(0), str.substring(1))}
}

