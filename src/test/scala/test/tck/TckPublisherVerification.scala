//package test.tck
//
//import core.{Flux, Mono}
//import org.reactivestreams.Publisher
//import org.reactivestreams.tck.{PublisherVerification, TestEnvironment}
//import org.scalatest.funsuite.AnyFunSuite
//import org.scalatest.testng.TestNGSuiteLike
//
//
//class TckPublisherVerification extends PublisherVerification[Int](new TestEnvironment(500L, 1000L)) with TestNGSuiteLike  {
//
//  override def createPublisher(elements: Long): Publisher[Int] = Mono.just(1).repeat().take(elements).asInstanceOf[Publisher[Int]]
//
//  override def createFailedPublisher(): Publisher[Int] = Mono.error(new RuntimeException("intentional mono error"))
//
//}
