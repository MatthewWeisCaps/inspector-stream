package test.mono

import java.util.concurrent.TimeUnit

import core.Mono
import org.scalatest.FunSuite
import reactor.test.StepVerifier
import reactor.test.scheduler.VirtualTimeScheduler
import test.StepVerifierExt._

import scala.concurrent.duration.Duration

class MonoZipWhenTest extends FunSuite {

  test("mono zipWhen") {

    VirtualTimeScheduler.getOrSet()

    val a = Mono.just("a").delayElement(Duration(1, TimeUnit.SECONDS))
    val b = (s: String) => Mono.just(s.toUpperCase)

    StepVerifier.withVirtualTime(() => a.zipWhen(b))
      .expectSubscription()
      .expectNoEvent(Duration(1, TimeUnit.SECONDS))
      .expectNext(("a", "A"))
      .expectComplete()
      .verify()
  }

  test("mono zipWhen combinator") {

    val a = Mono.just("a").delayElement(Duration(1, TimeUnit.SECONDS))
    val b = (s: String) => Mono.just(s.toUpperCase)

    val concatFn = (s1: String, s2: String) => s1 ++ s2

    StepVerifier.create(a.zipWhen(b, concatFn))
      .expectSubscription()
      .expectNext("aA")
      .expectComplete()
      .verify()
  }

  
}
