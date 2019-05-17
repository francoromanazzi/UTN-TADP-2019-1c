import org.scalatest.{BeforeAndAfter, FreeSpec, Matchers}

class PersonaSpec extends FreeSpec with Matchers with BeforeAndAfter{

  var unaPersona: Persona = null

  before {
    unaPersona = new Persona(20)
  }

  "Una persona" - {

      "debería poder cumplir años" in {
        unaPersona.cumpliAnio
        unaPersona.edad shouldBe 21
      }

      "debería mantener su edad si no cumple años" in {
        unaPersona.edad shouldBe 20
      }

  }
}
