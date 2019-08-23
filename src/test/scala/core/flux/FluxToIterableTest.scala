package core.flux

import core.Flux
import org.scalatest.{Assertions, FunSuite}

class FluxToIterableTest extends FunSuite {

  test("flux toIterable") {
    val iterable = Flux.just("a", "b", "c", "d", "e").toIterable.toSeq

    Assertions.assert(iterable(0) == "a")
    Assertions.assert(iterable(1) == "b")
    Assertions.assert(iterable(2) == "c")
    Assertions.assert(iterable(3) == "d")
    Assertions.assert(iterable(4) == "e")
  }

}
