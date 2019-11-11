package test.flux

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.SECONDS

import core.Flux
import org.scalatest.FunSuite
import reactor.test.StepVerifier
import reactor.test.scheduler.VirtualTimeScheduler
import test.StepVerifierExt._

import scala.concurrent.duration.{Duration, FiniteDuration}

class FluxAssertionsTest extends FunSuite {

  test("flux always positive test passing") {
    val flux: Flux[Int] = Flux.range(1, 100)
      .assertAlways(_ > 0)

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNextCount(100)
      .expectComplete()
      .verify()
  }

  test("flux always positive test failing") {
    val flux: Flux[Int] = Flux.range(1, 100)
      .map(_ - 50)
      .assertAlways(_ > 0)

    StepVerifier.create(flux)
      .expectSubscription()
      .expectError()
      .verify()
  }

  test("flux never negative test passing") {
    val flux: Flux[Int] = Flux.range(1, 100)
      .assertNever(_ < 0)

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNextCount(100)
      .expectComplete()
      .verify()
  }

  test("flux never negative test failing") {
    val flux: Flux[Int] = Flux.range(1, 100)
      .map(_ - 50)
      .assertNever(_ < 0)

    StepVerifier.create(flux)
      .expectSubscription()
      .expectError()
      .verify()
  }

  test("flux chaining assertions passing") {
    val flux: Flux[Int] = Flux.range(1, 100)
      .assertAlways((n: Int) => n > 0, "assertion 1")
      .map(_ * -1)
      .assertNever((n: Int) => n > 0, "assertion 2")

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNextCount(100)
      .expectComplete()
      .verify()
  }

  test("flux chaining assertions with first failing") {
    val flux: Flux[Int] = Flux.range(1, 100)
      .assertAlways((n: Int) => n < 0, "assertion 1")
      .map(_ * -1)
      .assertNever((n: Int) => n > 0, "assertion 2")

    StepVerifier.create(flux)
      .expectSubscription()
      .expectErrorMessage("assertion 1 ==> expected: <true> but was: <false>")
      .verify()
  }

  test("flux chaining assertions with second failing") {
    val flux: Flux[Int] = Flux.range(1, 100)
      .assertAlways((n: Int) => n > 0, "assertion 1")
      .map(_ * -1)
      .assertNever((n: Int) => n < 0, "assertion 2")

    StepVerifier.create(flux)
      .expectSubscription()
      .expectErrorMessage("assertion 2 ==> expected: <false> but was: <true>")
      .verify()
  }

  test("flux expect error after some pass") {
    val flux: Flux[Int] = Flux.range(1, 100)
      .map(n => 50 - n)
      .assertAlways(_ > 0)

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNext(49, 48, 47, 46, 45)
      .expectNextCount(40)
      .expectNext(4, 3, 2, 1)
      .expectError()
      .verify()
  }

  test("flux assertEmpty passing") {
    val flux: Flux[Int] = Flux.empty().assertEmpty()

    StepVerifier.create(flux)
      .expectSubscription()
      .expectComplete()
      .verify()
  }

  test("flux assertEmpty passing complex 1") {
    val flux: Flux[Int] = Flux.just(1, 2, 3)
      .map(_ + 10)
      .filter(_ < 10).assertEmpty()

    StepVerifier.create(flux)
      .expectSubscription()
      .expectComplete()
      .verify()
  }

  test("flux assertEmpty passing complex 2") {
    val fluxSupplier: () => Flux[Int] = () => Flux.just(1, 2, 3)
      .delaySequence(FiniteDuration(6, SECONDS))
      .take(FiniteDuration(5, SECONDS))
      .assertEmpty()

    StepVerifier.withVirtualTime(fluxSupplier)
      .expectSubscription()
      .expectNoEvent(FiniteDuration(5, SECONDS))
      .thenAwait(FiniteDuration(1, SECONDS))
      .expectComplete()
      .verify()
  }

  test("flux assertEmpty failing") {
    val flux: Flux[Int] = Flux.just(1, 2, 3).assertEmpty()

    StepVerifier.create(flux)
      .expectSubscription()
      .expectError()
      .verify()
  }

  test("flux assertEmpty passing complex") {
    val fluxSupplier: () => Flux[Int] = () => Flux.just(1, 2, 3)
      .delaySequence(FiniteDuration(4, SECONDS))
      .take(FiniteDuration(5, SECONDS))
      .assertEmpty()

    StepVerifier.withVirtualTime(fluxSupplier)
      .expectSubscription()
      .expectNoEvent(FiniteDuration(4, SECONDS))
      .expectError()
      .verify()
  }

  test("flux assertNotEmpty passing") {
    val flux: Flux[Int] = Flux.just(1, 2, 3)
      .assertNotEmpty()

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNext(1, 2, 3)
      .expectComplete()
      .verify()
  }

  test("flux assertNotEmpty failing") {
    val flux: Flux[Int] = Flux.just(1, 2, 3)
      .filter(_ > 10)
      .assertNotEmpty()

    StepVerifier.create(flux)
      .expectSubscription()
      .expectError()
      .verify()
  }

}
