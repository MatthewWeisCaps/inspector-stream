package core.mono

import core.Mono
import org.scalatest.FunSuite
import reactor.test.StepVerifier

class MonoFlatMapTest extends FunSuite {

  test("mono flatMap") {

    val mono = Mono.just("a").flatMap(letter => Mono.just(letter.toUpperCase()))

    StepVerifier.create(mono)
      .expectSubscription()
      .expectNext("A")
      .expectComplete()
      .verify()
  }

  
}
