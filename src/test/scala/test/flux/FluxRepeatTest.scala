package test.flux

import java.util.concurrent.TimeUnit.SECONDS

import core.Flux
import org.scalatest.FunSuite
import reactor.test.StepVerifier
import reactor.test.scheduler.VirtualTimeScheduler

import scala.concurrent.duration.Duration

import test.StepVerifierExt._

class FluxRepeatTest extends FunSuite {

  test("flux repeat repeats elements") {
    val flux: Flux[String] = Flux.just("a", "b", "c").repeat()

    val s = StepVerifier.create(flux.take(6))
      .expectSubscription()
      .expectNext("a", "b", "c")
      .expectNext("a", "b", "c")
      .expectComplete()
      .verify()
  }

  test("flux repeat respects time delays") {

    val scheduler = VirtualTimeScheduler.getOrSet()

    val flux: Flux[String] = Flux.just("a", "b", "c").delaySequence(Duration(1, SECONDS)).repeat()

    val s = StepVerifier.withVirtualTime(() => flux.take(6))
      .expectSubscription()
      .expectNoEvent(Duration(1, SECONDS))
      .expectNext("a", "b", "c")
      .expectNoEvent(Duration(1, SECONDS))
      .expectNext("a", "b", "c")
      .expectComplete()
      .verify()
  }

}
