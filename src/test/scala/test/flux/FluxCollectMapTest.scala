package test.flux

import core.Flux
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class FluxCollectMapTest extends AnyFunSuite {

  /**
    * IMPORTANT: collectMap returns a map allowing one value per key. See collectMultiMap for 1 key to N value mappings.
    */

  // creates a map of color -> Student(...)
  test("flux_collectMap") {

    case class Student(name: String, favoriteColor: String)

    val students = Flux.just(
      Student("alice", "red"),
      Student("bob", "blue"),
      Student("casey", "green"),
    )

    val map: Map[String, Student] = students.collectMap(_.favoriteColor).block()

    // ensure all keys in map with nothing extra
    assert(map.contains("red"))
    assert(map.contains("blue"))
    assert(map.contains("green"))
    assert(map.keySet.size == 3)

    // ensure all students in map with nothing extra
    assert(map.values.exists(_ == Student("alice", "red")))
    assert(map.values.exists(_ == Student("bob", "blue")))
    assert(map.values.exists(_ == Student("casey", "green")))
    assert(map.values.size == 3)

    // ensure all key/value pairs exist and are correct
    assert(map("red") == Student("alice", "red"))
    assert(map("blue") == Student("bob", "blue"))
    assert(map("green") == Student("casey", "green"))
  }

  // creates a map of color -> name
  test("flux_collectMap_valueExtractor") {

    case class Student(name: String, favoriteColor: String)

    val students = Flux.just(
      Student("alice", "red"),
      Student("bob", "blue"),
      Student("casey", "green"),
    )

    val map: Map[String, String] = students.collectMap(_.favoriteColor, _.name).block()

    // ensure all keys in map with nothing extra
    assert(map.contains("red"))
    assert(map.contains("blue"))
    assert(map.contains("green"))
    assert(map.keySet.size == 3)

    // ensure all students in map with nothing extra
    assert(map.values.exists(_ == "alice"))
    assert(map.values.exists(_ == "bob"))
    assert(map.values.exists(_ == "casey"))
    assert(map.values.size == 3)

    // ensure all key/value pairs exist and are correct
    assert(map("red") == "alice")
    assert(map("blue") == "bob")
    assert(map("green") == "casey")
  }

  // creates a map of color -> name
  test("flux collectMap valueExtractor+mapSupplier") {

    case class Student(name: String, favoriteColor: String)

    val students = Flux.just(
      Student("alice", "red"),
      Student("bob", "blue"),
      Student("casey", "green"),
    )

    // call collectMap using a map that already contains an entry
    val supplier = () => mutable.Map(("purple", "zack"))
    val map: Map[String, String] = students.collectMap(_.favoriteColor, _.name, supplier).block()

    // ensure all keys in map with nothing extra
    assert(map.contains("red"))
    assert(map.contains("blue"))
    assert(map.contains("green"))
    assert(map.contains("purple"))
    assert(map.keySet.size == 4)

    // ensure all students in map with nothing extra
    assert(map.values.exists(_ == "alice"))
    assert(map.values.exists(_ == "bob"))
    assert(map.values.exists(_ == "casey"))
    assert(map.values.exists(_ == "zack"))
    assert(map.values.size == 4)

    // ensure all key/value pairs exist and are correct
    assert(map("red") == "alice")
    assert(map("blue") == "bob")
    assert(map("green") == "casey")
    assert(map("purple") == "zack")
  }

}
