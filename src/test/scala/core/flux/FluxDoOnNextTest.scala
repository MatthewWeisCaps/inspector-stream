package core.flux

import core.Flux
import org.scalatest.FunSuite

class FluxDoOnNextTest extends FunSuite {

  test("doOnNextAssignment") {
    var sideEffect = "banana"

    Flux.just("apple")
      .doOnNext(s => sideEffect = s)
      .subscribe(_ => Unit, it => throw it)

    assert(sideEffect == "apple")
  }

  test("doOnNextAddition") {
    var sideEffect = 0

    Flux.just(1, 2, 3)
      .doOnNext(n => sideEffect += n)
      .subscribe()

    assert(sideEffect == 1 + 2 + 3)
  }

}
