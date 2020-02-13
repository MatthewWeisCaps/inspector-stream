package org.sireum.hamr.inspector.stream.tck.reactor

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

import org.reactivestreams.Publisher
import org.reactivestreams.tck.{PublisherVerification, TestEnvironment}
import org.sireum.hamr.inspector.stream.{Flux, Mono}

import scala.util.Random


/*
  * Port of Reactor's TCK:
  * https://github.com/reactor/reactor-core/blob/master/reactor-core/src/test/java/reactor/core/publisher/tck/AbstractFluxVerification.java
  *
  * MUST BE RUN WITH TESTNG.
  */
abstract class AbstractFluxVerification extends PublisherVerification[Int](new TestEnvironment()) {

  private val counters: java.util.Map[Thread, AtomicLong] = new ConcurrentHashMap[Thread, AtomicLong]()

  val batch = 1024;

  def transformFlux(f: Flux[Int]): Flux[Int]

  override def createPublisher(elements: Long): Publisher[Int] = {
    if (elements <= Integer.MAX_VALUE) {
      return Flux.range(1, elements.toInt)
          .filter(_ => true)
          .map(integer => integer)
          .transform(this.transformFlux)
    }
    else {
      val random = new Random()

      return Mono.fromCallable(() => random.nextInt())
          .repeat()
          .map(Math.abs)
          .transform(this.transformFlux)
    }
  }

  override def createFailedPublisher(): Publisher[Int] = Flux.error[Int](new Exception("boom")).transform(this.transformFlux)

  protected def monitorThreadUse(value: Any): Unit = {
    var counter = counters.get(Thread.currentThread())
    if (counter == null) {
      counter = new AtomicLong()
      counters.put(Thread.currentThread(), counter)
    }
    counter.incrementAndGet()
  }

}
