package eu.inn.parser

import org.scalatest.{FreeSpec, Matchers}

class ExpressionEngineTest extends FreeSpec with Matchers {

  val evalEngine = new MapBasedEvaluationEngine(Map(
    "user" → Map(
      "isDefined" → true,
      "roles" → Seq("dev", "qa", "ops")
    )
  ))
  val exprEngine = new ExpressionEngine(evalEngine)

  "ExpressionEngine" - {
    "simple expressions" in {
      exprEngine.parse("user.isDefined") shouldBe true
      exprEngine.parse("!user.isDefined") shouldBe false
      exprEngine.parse("user.isDefined = true") shouldBe true
      exprEngine.parse("user.isDefined != true") shouldBe false
      exprEngine.parse("""user.roles has 'qa' """) shouldBe true
      exprEngine.parse("""user.roles has "admin" """) shouldBe false
    }

    "complex expressions" in {
      exprEngine.parse("""(user.isDefined = true) and (user.roles has "admin")""") shouldBe false
      exprEngine.parse("""(user.isDefined = true) and (user.roles has "qa")""") shouldBe true
      exprEngine.parse("""(user.isDefined) or (user.roles has "qa")""") shouldBe true
      exprEngine.parse("""(!user.isDefined) or (user.roles has "qa")""") shouldBe true
      exprEngine.parse("""(!user.isDefined) or (user.roles has "admin")""") shouldBe false
    }

    "ip and ip range expressions" in {
      exprEngine.parse("""10.10.10.10 in (10.10.0.0 - 10.10.20.20)""") shouldBe true
      exprEngine.parse("""10.10.10.10 in 10.10.0.0 - 10.10.9.9""") shouldBe false
      exprEngine.parse("""10.10.10.10 not in 10.10.0.0 - 10.10.20.20""") shouldBe false
      exprEngine.parse("""10.10.10.10 not in 10.10.0.0 - 10.10.9.9""") shouldBe true
    }
  }
}
