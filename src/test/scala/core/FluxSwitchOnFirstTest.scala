package core

import org.reactivestreams.Publisher
import org.scalatest.FunSuite
import reactor.test.StepVerifier
import test.StepVerifierExt._

//import scala.language.implicitConversions

class FluxSwitchOnFirstTest extends FunSuite {

  test("flux switchOnFirst") {

    val flux = Flux.just(3, 5, 1, 9, 6, 2, 0, 4)
      .switchOnFirst((s, flux) => {
        if (s.hasValue) {
          flux.filter(n => n >= s.get())
        } else {
          flux
        }
      })

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNext(3)
      .expectNext(5)
      // skip 1 because 1 is not greater than or equal to 3
      .expectNext(9)
      .expectNext(6)
      // skip 2 because 2 is not greater than or equal to 3
      // skip 0 because 0 is not greater than or equal to 3
      .expectNext(4)
      .expectComplete()
      .verify()
  }

}
