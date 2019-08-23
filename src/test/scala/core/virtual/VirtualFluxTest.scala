//package core
//
//import java.util.function.Supplier
//import java.util.concurrent.{CountDownLatch, TimeUnit}
//import java.util.concurrent.TimeUnit._
//import java.time.{Duration => JDuration}
//
//import org.reactivestreams.{Subscriber, Subscription}
//import org.scalatest._
//import reactor.core.scala.publisher.SFlux
//import reactor.core.scheduler.Schedulers
//import reactor.reactor.test.StepVerifier
//import reactor.test.scheduler.VirtualTimeScheduler
//
//import scala.concurrent.duration.Duration
//
///**
//  * Notes about virtual timed flux:
//  *
//  *   (1) Delays are relative to the start time of the subscription, not time 0.
//  *   (2) This means that any historic/replay fluxes should be paired with a new VirtualTimeScheduler
//  *
//  * TODO:
//  *
//  * (1) create Scala lib wrapping Reactor-Core (Not the scala extension with weird operators)
//  * (2) write custom virtual time scheduler?
//  *
//  * OR
//  *
//  * (1) Create custom impl based closely on reactor
//  * (2) All hot subscribers of art are on initial stage used when events come in
//  * (3) Since art schedules things in "cycles" (forgetting word for periodic start->stop bursts), comparison ops should be aware of this
//  * (4) This ensures joined streams occur across the same "cycles"
//  *
//  *
//  */
//class VirtualFluxTest extends FlatSpec with Matchers {
//
//  "VirtualFlux" should "recieve all values based on its internal scheduler" in {
//    val stream = SFlux.just(
//      Timed(1, Duration(1, SECONDS)),
//      Timed(2, Duration(2, SECONDS)),
//      Timed(3, Duration(2, SECONDS)),
//    )
////
////    val vts = new Supplier[_ <: VirtualTimeScheduler] {
////      override def get(): VirtualTimeScheduler = VirtualTimeScheduler.get()
////    }
////    VirtualTimeScheduler.get().advanceTimeBy(JDuration.ofSeconds(3))
////
////    StepVerifier.create(VirtualFlux.virtualize(stream))
////      .expectSubscription()
////      .expectNoEvent(JDuration.ofSeconds(3))
////      .thenCancel()
////      .verify()
////
////    VirtualTimeScheduler.get().advanceTimeBy(JDuration.ofSeconds(1))
////
////    StepVerifier.create(VirtualFlux.virtualize(stream))
////      .expectSubscription()
////      .expectNext(1)
////      .expectNoEvent(JDuration.ofSeconds(3))
////      .thenCancel()
////      .verify()
////
//
//    // if the ONLY time advance is before subscription, it will not work because the subscription will have a "starttime"
//    // at 4 seconds, and all delays are relative to this start time.
////    VirtualTimeScheduler.get().advanceTimeBy(JDuration.ofSeconds(4))
//
//    val l = new CountDownLatch(1)
//    val v = VirtualFlux.virtualize(stream)
//    v._1.take(3).doOnNext(it => {
//      println(it)
//    }).subscribe()
//
//    v._2.advanceTimeBy(JDuration.ofSeconds(4))
//
//
//    // advancing scheduler after subscription works because subscription gets behind-the-scenes start time based on
//    // virtual scheduler's time
//    VirtualTimeScheduler.get().advanceTimeBy(JDuration.ofSeconds(4))
//
//
//    while (l.getCount > 0) {
//      VirtualTimeScheduler.get().advanceTime() // refresh / recheck
////      VirtualTimeScheduler.get().advanceTimeBy(JDuration.ofSeconds(1))
//    }
//
////    StepVerifier.create(VirtualFlux.virtualize(stream).take(3))
////      .expectSubscription()
////      .expectNext(1)
////      .expectNext(2)
////      .expectNext(3)
////      .expectComplete()
////      .verify()
//  }
//
//}
