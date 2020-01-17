//package test.flux
//
//import java.time.Instant
//import java.util.concurrent.TimeUnit
//import java.util.concurrent.TimeUnit._
//
//import core.Flux
//import org.scalatest.Assertions
//import reactor.core.publisher.SynchronousSink
//import reactor.test.StepVerifierOptions.create
//import reactor.test.{StepVerifier, StepVerifierOptions}
//import reactor.test.scheduler.VirtualTimeScheduler
//
//import scala.concurrent.duration.Duration
//import test.StepVerifierExt._
//
//class FluxCacheTest extends AnyFunSuite {
//
//  test("flux cache respec") {
//
//    VirtualTimeScheduler.getOrSet()
//    VirtualTimeScheduler.get().advanceTimeBy(Duration.fromNanos(Long.MaxValue))
//
//    //    val flux: Flux[Int] = Flux.generate[Int]((sink: SynchronousSink[Int]) => {
////      sink.next(scheduler.now(TimeUnit.SECONDS).toInt)
////      if (scheduler.now(TimeUnit.SECONDS) >= 3 - start) {
////        sink.complete()
////      }
////    }).cache()
//
////    val flux = Flux.range(1, 10).delayElements(Duration(1, SECONDS), VirtualTimeScheduler.get()).cache()
////      .doOnSubscribe(_ => VirtualTimeScheduler.get().advanceTimeBy(Duration(1, SECONDS)))
////        .doOnEach(_ => VirtualTimeScheduler.get().advanceTimeBy(Duration(1, SECONDS)))
//
////    val flux = Flux.range(1, 10).delayElements(Duration(1, SECONDS), VirtualTimeScheduler.get()).cache()
////      .doOnSubscribe(_ => VirtualTimeScheduler.get().advanceTime())
////      .doOnEach(_ => VirtualTimeScheduler.get().advanceTime())
//
//    val flux1 = Flux.range(1, 10).delayElements(Duration(1, SECONDS)).doOnEach(_ => VirtualTimeScheduler.get().advanceTime())
//    val flux2 = Flux.range(11, 10).delayElements(Duration(3, SECONDS)).doOnEach(_ => VirtualTimeScheduler.get().advanceTime())
//
//    flux1.timestamp().zipWith(flux2.timestamp()).doOnEach(it => println(s"${it.get()._1._1}, ${it.get()._2._1}")).map(it => (it._1._2, it._2._2)).map(tuple => tuple._1 + tuple._2).doOnEach(println).blockLast()
//
//
////    flux
////        .doOnEach(println)
////        .doOnEach(_ => VirtualTimeScheduler.get().advanceTime())
//////        .doOnSubscribe(_ => VirtualTimeScheduler.get().advanceTimeBy(Duration(1, SECONDS)))
////        .take(10)
////        .blockLast()
////
////    StepVerifier.withVirtualTime(() => flux.take(6))
////      .expectSubscription()
////      .as("subscribe a third time expecting cached values instantly")
////      .expectNext(1, 2, 3, 4, 5, 6)
////      .expectComplete()
////      .verify()
//
////    StepVerifier.withVirtualTime(() => flux.take(3))
////      .expectSubscription()
////      .as("subscribe")
////      .thenRun(() => println(VirtualTimeScheduler.get().now(TimeUnit.SECONDS)))
//////      .thenAwait(Duration(1, SECONDS))
////      .thenRun(() => println(VirtualTimeScheduler.get().now(TimeUnit.SECONDS)))
////      .expectNext(1)
////      .thenRun(() => println(VirtualTimeScheduler.get().now(TimeUnit.SECONDS)))
//////      .thenAwait(Duration(1, SECONDS))
////      .expectNext(2)
////      .thenRun(() => println(VirtualTimeScheduler.get().now(TimeUnit.SECONDS)))
//////      .thenAwait(Duration(1, SECONDS))
////      .expectNext(3)
////      .thenRun(() => println(VirtualTimeScheduler.get().now(TimeUnit.SECONDS)))
////      .expectComplete()
////      .verify()
////
////    StepVerifier.withVirtualTime(() => flux.take(6))
////      .expectSubscription()
////      .as("subscribe again - 1: expecting to receive cached values instantly")
////      .thenRun(() => println(VirtualTimeScheduler.get().now(TimeUnit.SECONDS)))
////      .thenAwait(Duration(3, SECONDS))
////      .expectNext(1, 2, 3)
////      .as("subscribe again - 2: expecting further values")
////      .thenAwait(Duration(1, SECONDS))
////      .expectNext(4)
////      .thenRun(() => println(VirtualTimeScheduler.get().now(TimeUnit.SECONDS)))
////      .thenAwait(Duration(1, SECONDS))
////      .expectNext(5)
////      .thenRun(() => println(VirtualTimeScheduler.get().now(TimeUnit.SECONDS)))
////      .thenAwait(Duration(1, SECONDS))
////      .expectNext(6)
////      .expectComplete()
////      .verify()
////
////    StepVerifier.withVirtualTime(() => flux.take(6))
////      .expectSubscription()
////      .as("subscribe a third time expecting cached values instantly")
////      .expectNext(1, 2, 3, 4, 5, 6)
////      .expectComplete()
////      .verify()
//  }
//
//}
