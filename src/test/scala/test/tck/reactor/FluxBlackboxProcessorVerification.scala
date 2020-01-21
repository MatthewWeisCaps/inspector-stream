package test.tck.reactor

import java.util.concurrent.TimeUnit

import core.Flux
import org.testng.annotations.{AfterTest, BeforeTest, Test}
import reactor.core.scheduler.{Scheduler, Schedulers}

import scala.concurrent.duration.FiniteDuration


/*
  * Port of Reactor's TCK:
  * https://github.com/reactor/reactor-core/blob/master/reactor-core/src/test/java/reactor/core/publisher/tck/FluxBlackboxProcessorVerification.java
  *
  * MUST BE RUN WITH TESTNG.
  */
@Test
final class FluxBlackboxProcessorVerification extends AbstractFluxVerification {

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

  @BeforeTest
  def beforeEach(): Unit = {
    sharedGroup = Schedulers.newParallel("fluxion-tck", 2)
  }

  @AfterTest
  def afterEach(): Unit = {
    sharedGroup.dispose()
  }

}








