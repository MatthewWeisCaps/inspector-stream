package org.sireum.hamr.inspector.stream.tck

import org.reactivestreams.Publisher
import org.reactivestreams.tck.{PublisherVerification, TestEnvironment}
import org.scalatest.TestSuite
import org.sireum.hamr.inspector.stream.Mono
import org.testng.annotations.Test
import org.testng.ITestNGMethod

// must be run with TestNG
class TckPublisherVerification extends PublisherVerification[Int](new TestEnvironment()) {

  override def createPublisher(elements: Long): Publisher[Int] = Mono.just(1).repeat().take(elements).asInstanceOf[Publisher[Int]]

  override def createFailedPublisher(): Publisher[Int] = Mono.error(new RuntimeException("intentional mono error"))

}
