package core.mono

import core.Mono
import org.scalatest.FunSuite
import reactor.test.StepVerifier

class MonoZipWithTest extends FunSuite {

  test("mono zipWith") {

    val a = Mono.just("a")
    val b = Mono.just("b")

    StepVerifier.create(a.zipWith(b))
      .expectSubscription()
      .expectNext(("a", "b"))
      .expectComplete()
      .verify()
  }

  test("mono zipWith combinator") {

    val a = Mono.just("a")
    val b = Mono.just("b")

    val concatFn = (s1: String, s2: String) => s1 ++ s2

    StepVerifier.create(a.zipWith(b, concatFn))
      .expectSubscription()
      .expectNext("ab")
      .expectComplete()
      .verify()
  }

  
}
