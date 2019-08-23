package core

import org.scalatest.FunSuite
import reactor.test.StepVerifier
import test.StepVerifierExt._

class FluxZipTest extends FunSuite {

  test("zip") {
    val flux = Flux.zip(Flux.just("a"), Flux.just("b"), Flux.just("c"))

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNext(("a", "b", "c"))
      .expectComplete()
      .verify()
  }

  test("zipMap") {
    val flux = Flux.zip(Flux.just("a"), Flux.just("b"), Flux.just("c"))

    StepVerifier.create(flux.map(((a: String, b: String, c: String) => a + b + c).tupled))
      .expectSubscription()
      .expectNext("abc")
      .expectComplete()
      .verify()
  }
}
