package core.tck

import core.{Flux, Mono}
import org.reactivestreams.Publisher
import org.reactivestreams.tck.{PublisherVerification, TestEnvironment}
import org.scalatest.FunSuite


class TckTest extends PublisherVerification[Int](new TestEnvironment()) {

  override def createPublisher(elements: Long): Publisher[Int] = Flux.range(1, elements.toInt).asInstanceOf[Publisher[Int]]

  override def createFailedPublisher(): Publisher[Int] = Mono.error(new RuntimeException("intentional mono error"))

}
