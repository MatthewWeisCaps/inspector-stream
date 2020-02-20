package org.sireum.hamr.inspector.stream.flux

import org.scalatest.funsuite.AnyFunSuite
import org.sireum.hamr.inspector.stream.Flux

class FluxDoOnNextTest extends AnyFunSuite {

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