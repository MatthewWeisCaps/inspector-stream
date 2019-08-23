package test.flux

import core.Flux
import org.scalatest.FunSuite
import reactor.test.StepVerifier

//import scala.language.implicitConversions

class FluxCombineLatestTest extends FunSuite {

  test("flux combineLatest") {

    val letters = Flux.just("a", "b", "c")
    val numbers = Flux.just(1, 2, 3)

    val flux = Flux.combineLatest[String, Int, (String, Int)](letters, numbers, (s: String, n: Int) => (s, n))

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNext(("c", 1))
      .expectNext(("c", 2))
      .expectNext(("c", 3))
      .expectComplete()
      .verify()
  }

}
