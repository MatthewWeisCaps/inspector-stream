package core

import org.scalatest.FunSuite

import scala.collection.mutable

class FluxCollectMultiMapTest extends FunSuite {

  test("flux collect Multimap") {

    case class Student(name: String, favoriteColor: String)

    val students = Flux.just(
      Student("alice", "red"),
      Student("bob", "blue"),
      Student("casey", "green"),
      Student("darell", "blue"),
      Student("ellen", "red"),
    )

    val map: Map[String, Iterable[Student]] = students.collectMultimap(_.favoriteColor).block()

    // ensure all keys in map with nothing extra
    assert(map.contains("red"))
    assert(map.contains("blue"))
    assert(map.contains("green"))
    assert(map.keySet.size == 3)

    // ensure all students in map with nothing extra
    assert(map.values.exists(_.exists(_ == Student("alice", "red"))))
    assert(map.values.exists(_.exists(_ == Student("bob", "blue"))))
    assert(map.values.exists(_.exists(_ == Student("casey", "green"))))
    assert(map.values.exists(_.exists(_ == Student("darell", "blue"))))
    assert(map.values.exists(_.exists(_ == Student("ellen", "red"))))
    // ensure three iterables are contained in the value set (one for each color)
    assert(map.values.size == 3)
    // ensure the sum of elements across these iterables equals the number of people
    assert(map.values.foldLeft(0)((count: Int, it: Iterable[_]) => count + it.size) == 5)

    // ensure all key/value pairs exist and are correct
    assert(map("red").exists(_ == Student("alice", "red")))
    assert(map("blue").exists(_ == Student("bob", "blue")))
    assert(map("green").exists(_ == Student("casey", "green")))
    assert(map("blue").exists(_ == Student("darell", "blue")))
    assert(map("red").exists(_ == Student("ellen", "red")))
  }

  // creates a map of color -> name
  test("flux collect Multimap: valueExtractor") {

    case class Student(name: String, favoriteColor: String)

    val students = Flux.just(
      Student("alice", "red"),
      Student("bob", "blue"),
      Student("casey", "green"),
      Student("darell", "blue"),
      Student("ellen", "red"),
    )

    val map: Map[String, Iterable[String]] = students.collectMultimap(_.favoriteColor, _.name).block()

    // ensure all keys in map with nothing extra
    assert(map.contains("red"))
    assert(map.contains("blue"))
    assert(map.contains("green"))
    assert(map.keySet.size == 3)

    // ensure all students in map with nothing extra
    assert(map.values.exists(_.exists(_ == "alice")))
    assert(map.values.exists(_.exists(_ == "bob")))
    assert(map.values.exists(_.exists(_ == "casey")))
    assert(map.values.exists(_.exists(_ == "darell")))
    assert(map.values.exists(_.exists(_ == "ellen")))
    // ensure three iterables are contained in the value set (one for each color)
    assert(map.values.size == 3)
    // ensure the sum of elements across these iterables equals the number of people
    assert(map.values.foldLeft(0)((count: Int, it: Iterable[_]) => count + it.size) == 5)

    // ensure all key/value pairs exist and are correct
    assert(map("red").exists(_ == "alice"))
    assert(map("blue").exists(_ == "bob"))
    assert(map("green").exists(_ == "casey"))
    assert(map("blue").exists(_ == "darell"))
    assert(map("red").exists(_ == "ellen"))
  }

  // todo currently disabled with the hope this method can eventually be implemented without a map copy
//  // creates a map of color -> name
//  test("flux collectMultimap: valueExtractor, mapSupplier") {
//
//    case class Student(name: String, favoriteColor: String)
//
//    val students = Flux.just(
//      Student("alice", "red"),
//      Student("bob", "blue"),
//      Student("casey", "green"),
//      Student("darell", "blue"),
//      Student("ellen", "red"),
//    )
//
//    val supplier: () => mutable.Map[String, mutable.Iterable[String]] = () => mutable.Map(("purple", mutable.Iterable("zack")))
//    val m = supplier()
//    val s2 = () => m
//    val map: Map[String, Iterable[String]] = students.collectMultimap(_.favoriteColor, _.name, s2).block()
//    val m2: Map[String, Iterable[String]] = students.collectMultimap(_.favoriteColor, _.name, s2).block()
//
//    // ensure all keys in map with nothing extra
//    assert(map.contains("red"))
//    assert(map.contains("blue"))
//    assert(map.contains("green"))
//    assert(map.contains("purple"))
//    assert(map.keySet.size == 4)
//
//    // ensure all students in map with nothing extra
//    assert(map.values.exists(_.exists(_ == "alice")))
//    assert(map.values.exists(_.exists(_ == "bob")))
//    assert(map.values.exists(_.exists(_ == "casey")))
//    assert(map.values.exists(_.exists(_ == "darell")))
//    assert(map.values.exists(_.exists(_ == "ellen")))
//    assert(map.values.exists(_.exists(_ == "zack")))
//    // ensure four iterables are contained in the value set (one for each color)
//    assert(map.values.size == 4)
//    // ensure the sum of elements across these iterables equals the number of people
//    assert(map.values.foldLeft(0)((count: Int, it: Iterable[_]) => count + it.size) == 6)
//
//    // ensure all key/value pairs exist and are correct
//    assert(map("red").exists(_ == "alice"))
//    assert(map("blue").exists(_ == "bob"))
//    assert(map("green").exists(_ == "casey"))
//    assert(map("blue").exists(_ == "darell"))
//    assert(map("red").exists(_ == "ellen"))
//    assert(map("purple").exists(_ == "zack"))
//  }

}
