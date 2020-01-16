package test.tck.reactor

import java.util.concurrent.{ConcurrentHashMap, TimeUnit}
import java.util.concurrent.atomic.AtomicLong

import core.{Flux, Mono}
import org.reactivestreams.Publisher
import org.reactivestreams.tck.{PublisherVerification, TestEnvironment}
import org.scalatest.{BeforeAndAfterEach, FunSuite, Suite}
import reactor.core.scheduler.{Scheduler, Schedulers}

import scala.concurrent.duration.FiniteDuration
import scala.util.Random


/**
  * Port of Reactor's TCK:
  * https://github.com/reactor/reactor-core/blob/master/reactor-core/src/test/java/reactor/core/publisher/tck/FluxBlackboxProcessorVerification.java
  */
abstract class FluxBlackboxProcessorVerification extends AbstractFluxVerification with Suite with BeforeAndAfterEach {

  private var sharedGroup: Scheduler = null

  override def transformFlux(f: Flux[Int]): Flux[Int] = {
    val otherStream = Flux.just("test", "test2", "test3")
    val asyncGroup = Schedulers.newParallel("flux-p-tck", 2)

    val combinator = (t1: Int, t2: String) => t1

    return f.publishOn(sharedGroup)
        .parallel(2)
        .groups()
        .flatMap(stream => stream.publishOn(asyncGroup)
          .doOnNext(monitorThreadUse)
          .scan((prev, next) => next)
          .map(integer => -integer)
          .filter(integer => integer <= 0)
          .map(integer => -integer)
          .bufferTimeout(batch, FiniteDuration(50, TimeUnit.MILLISECONDS))
          .flatMap(Flux.fromIterable)
          .flatMap(i => Flux.zip(Flux.just(i), otherStream, combinator)))
        .publishOn(sharedGroup)
        .doAfterTerminate(() => asyncGroup.dispose())
        .doOnError(throwable => throwable.printStackTrace())
  }

  override protected def beforeEach(): Unit = {
    sharedGroup = Schedulers.newParallel("fluxion-tck", 2)
  }

  override protected def afterEach(): Unit = {
    sharedGroup.dispose()
  }

}








