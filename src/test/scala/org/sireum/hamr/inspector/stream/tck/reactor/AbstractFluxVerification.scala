/*
 * Copyright (c) 2020, Matthew Weis, Kansas State University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
