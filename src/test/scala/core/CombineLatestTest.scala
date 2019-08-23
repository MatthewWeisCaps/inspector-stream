package core

import org.reactivestreams.Publisher
import org.scalatest.FunSuite
import reactor.test.StepVerifier
import test.StepVerifierExt._

//import scala.language.implicitConversions

class CombineLatestTest extends FunSuite {

  test("combineLatest") {

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
