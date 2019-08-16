package core

import org.reactivestreams.Publisher
import org.scalatest.FunSuite
import reactor.test.StepVerifier

import scala.language.implicitConversions

class SwitchOnNextTest extends FunSuite {

  type ¬[A] = A => Nothing
  type ∨[T, U] = ¬[¬[T] with ¬[U]]
  type ¬¬[A] = ¬[¬[A]]
  type |∨|[T, U] = { type λ[X] = ¬¬[X] <:< (T ∨ U) }

  private implicit def coerce[T : (Int |∨| String)#λ](t : T): Any = t match {
    case n: Int => n
    case s: String => s
  }


  test("switchOnNext") {

    val letters = Flux.just("a", "b", "c")
    val numbers = Flux.just(1, 2, 3)
//    val numbers = Flux.just("1", "2", "3")

    val both = Flux.just(letters, numbers)
//    val both: Publisher[Publisher[_ <: Any]] = Flux.just(letters, numbers)


    val flux: Flux[Any] = Flux.switchOnNext(both)


    StepVerifier.create(flux)
      .expectSubscription()
      .expectNext("a")
      .expectNext("b")
      .expectNext("c")
//      .expectNext("1")
//      .expectNext("2")
//      .expectNext("3")
      .expectNext(1)
      .expectNext(2)
      .expectNext(3)
      .expectComplete()
      .verify()
  }

}
