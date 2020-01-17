package test.mono

import core.Mono
import org.scalatest.funsuite.AnyFunSuite
import reactor.test.StepVerifier

class MonoFlatMapTest extends AnyFunSuite {

  test("mono flatMap") {

    val mono = Mono.just("a").flatMap(letter => Mono.just(letter.toUpperCase()))

    StepVerifier.create(mono)
      .expectSubscription()
      .expectNext("A")
      .expectComplete()
      .verify()
  }

  
}
