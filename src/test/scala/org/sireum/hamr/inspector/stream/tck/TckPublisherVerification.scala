package org.sireum.hamr.inspector.stream.tck

import org.reactivestreams.Publisher
import org.reactivestreams.tck.{PublisherVerification, TestEnvironment}
import org.sireum.hamr.inspector.stream.Mono

// must be run with TestNG
class TckPublisherVerification extends PublisherVerification[Int](new TestEnvironment()) {

  override def createPublisher(elements: Long): Publisher[Int] = Mono.just(1).repeat().take(elements).asInstanceOf[Publisher[Int]]

  override def createFailedPublisher(): Publisher[Int] = Mono.error(new RuntimeException("intentional mono error"))

}
