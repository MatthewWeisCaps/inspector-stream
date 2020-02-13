package org.sireum.hamr.inspector.stream.tck.reactor

import java.util.concurrent.TimeUnit

import org.reactivestreams.Publisher
import org.reactivestreams.tck.{PublisherVerification, TestEnvironment}
import org.scalatest.{BeforeAndAfterEach, Suite}
import org.sireum.hamr.inspector.stream.Flux
import org.testng.annotations.Test
import reactor.core.scheduler.{Scheduler, Schedulers}

import scala.concurrent.duration.FiniteDuration


/**
  * Port of Reactor's TCK:
  * https://github.com/reactor/reactor-core/blob/master/reactor-core/src/test/java/reactor/core/publisher/tck/FluxSwitchOnFirstVerification.java
  *
  * MUST BE RUN WITH TESTNG.
  */
@Test
class FluxSwitchOnFirstVerification extends PublisherVerification[Int](new TestEnvironment()) {

  override def createPublisher(elements: Long): Publisher[Int] = {
    Flux.range(0, if (Int.MaxValue < elements) Int.MaxValue else elements.toInt)
      .switchOnFirst((first, innerFlux) => innerFlux)
  }

  override def createFailedPublisher(): Publisher[Int] = {
    Flux.error[Int](new RuntimeException())
      .switchOnFirst((first, innerFlux) => innerFlux)
  }

}








