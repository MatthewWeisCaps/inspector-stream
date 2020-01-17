package test

/**
  * A copy of TestSupport from:
  * https://github.com/reactor/reactor-scala-extensions/blob/master/src/test/scala/reactor/core/scala/publisher/TestSupport.scala
  */
trait TestSupport {
  sealed trait Vehicle
  case class Sedan(id: Int) extends Vehicle
  case class Truck(id: Int) extends Vehicle
}
