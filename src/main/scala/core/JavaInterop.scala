package core

import reactor.core.publisher.{Flux => JFlux}
import reactor.core.publisher.{ConnectableFlux => JConnectableFlux}
import reactor.core.publisher.{Mono => JMono}
import java.lang.{Iterable => JIterable}
import java.time.{Duration => JDuration}
import java.lang.{Runnable => JRunnable}
import java.lang.{Iterable => JIterable}
import java.util.{Map => JMap}
import java.util
import java.util.concurrent.{CompletableFuture => JCompletableFuture, CompletionStage}
import java.util.function.{Consumer => JConsumer}
import java.util.function.{Function => JFunction}
import java.util.function.{BiFunction => JBiFunction}

import reactor.core.publisher.{GroupedFlux => JGroupedFlux}
import org.reactivestreams.Publisher
import reactor.util.function.Tuples

import scala.collection.{JavaConverters, mutable}
import scala.collection.JavaConverters.asJavaIterable
import scala.collection.parallel.immutable
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

object JavaInterop {

  def wrapFlux[T](jFlux: JFlux[T]): Flux[T] = new FluxImpl[T](jFlux)
  def wrapConnectableFlux[T](jConnectableFlux: JConnectableFlux[T]): ConnectableFlux[T] = new ConnectableFlux[T](jConnectableFlux)
  def wrapGroupedFlux[K, V](jGroupedFlux: JGroupedFlux[K, V]): GroupedFlux[K, V] = new GroupedFlux[K, V](jGroupedFlux)
  def wrapMono[T](jMono: JMono[T]): Mono[T] = new MonoImpl[T](jMono)

  def toScalaSeq[T](list: java.util.List[T]): Seq[T] = JavaConverters.asScalaIteratorConverter(list.iterator()).asScala.toSeq
  def toScalaMap[K, V](map: java.util.Map[K, V]): Map[K, V] = JavaConverters.mapAsScalaMapConverter(map).asScala.toMap

  def toJavaMap[K, V](map: Map[K, V]): JMap[K, V] = JavaConverters.mapAsJavaMap(map)
  def toJavaMutableMap[K, V](map: mutable.Map[K, V]): JMap[K, V] = JavaConverters.mutableMapAsJavaMap(map)


  def toScalaFuture[T](completableFuture: CompletionStage[T]): Future[T] = scala.compat.java8.FutureConverters.toScala(completableFuture)
//  def toScalaFuture[T](completableFuture: JCompletableFuture[T]): Future[T] = scala.compat.java8.FutureConverters.toScala(completableFuture)


  def toScalaIterable[T](collection: util.Collection[T]): Iterable[T] = JavaConverters.collectionAsScalaIterable(collection)
  def toScalaIterable[T](iterable: JIterable[T]): Iterable[T] = JavaConverters.iterableAsScalaIterable(iterable)
  def toJavaCollection[T](iterable: mutable.Iterable[T]): util.Collection[T] = JavaConverters.asJavaCollection(iterable)
  def toJavaCollection[T](iterable: Iterable[T]): util.Collection[T] = JavaConverters.asJavaCollection(iterable)


  // tuples
  def toReactorTuple2[T1, T2](tuple: (T1, T2)): reactor.util.function.Tuple2[T1, T2] = Tuples.of(tuple._1, tuple._2)
  def toReactorTuple3[T1, T2, T3](tuple: (T1, T2, T3)): reactor.util.function.Tuple3[T1, T2, T3] = Tuples.of(tuple._1, tuple._2, tuple._3)
  def toReactorTuple4[T1, T2, T3, T4](tuple: (T1, T2, T3, T4)): reactor.util.function.Tuple4[T1, T2, T3, T4] = Tuples.of(tuple._1, tuple._2, tuple._3, tuple._4)
  def toReactorTuple5[T1, T2, T3, T4, T5](tuple: (T1, T2, T3, T4, T5)): reactor.util.function.Tuple5[T1, T2, T3, T4, T5] = Tuples.of(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5)
  def toReactorTuple6[T1, T2, T3, T4, T5, T6](tuple: (T1, T2, T3, T4, T5, T6)): reactor.util.function.Tuple6[T1, T2, T3, T4, T5, T6] = Tuples.of(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6)
  def toReactorTuple7[T1, T2, T3, T4, T5, T6, T7](tuple: (T1, T2, T3, T4, T5, T6, T7)): reactor.util.function.Tuple7[T1, T2, T3, T4, T5, T6, T7] = Tuples.of(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, tuple._7)
  def toReactorTuple8[T1, T2, T3, T4, T5, T6, T7, T8](tuple: (T1, T2, T3, T4, T5, T6, T7, T8)): reactor.util.function.Tuple8[T1, T2, T3, T4, T5, T6, T7, T8] = Tuples.of(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, tuple._7, tuple._8)

  //  def asJavaCollection[T](iterable: Iterable[T]): util.Collection[T] = JavaConverters.asJavaCollection(iterable)
//  def asJavaIterable[T](iterable: Iterable[T]): JIterable[T] = asJavaIterable(iterable)

  //  def asJavaDuration(finiteDuration: FiniteDuration): JDuration = JDuration.ofNanos(finiteDuration.toNanos)
//
//
//  // Known as a Runnable in the java world
//  def asJavaRunnable[T](runnable: () => Unit): JRunnable = () => runnable.apply()
//
//  // Known as a Consumer in the java world
//  def asJavaConsumer[T](consumer: T => Unit): JConsumer[T] = (t: T) => consumer.apply(t)
//
//  // Known as a Function in the java world
//  def asJavaFn1[T, R](function: T => R): JFunction[T, R] = (t: T) => function.apply(t)
//
//  // Known as a BiFunction in the java world
//  def asJavaFn2[T, U, R](function: (T, U) => R): JBiFunction[T, U, R] = (t: T, u: U) => function.apply(t, u)
//
//  // Does not exist in the java world (without @FunctionalInterface), so follow convention of reactor by converting to function of Array[AnyRef]
//  def asJavaFn3[T1 >: AnyRef, T2 >: AnyRef, T3 >: AnyRef, R](function: (T1, T2, T3) => R): JFunction[Array[AnyRef], R] = (arr: Array[AnyRef]) => function.apply(arr(0), arr(1), arr(2))
//
//  // Does not exist in the java world (without @FunctionalInterface), so follow convention of reactor by converting to function of Array[AnyRef]
//  def asJavaFn4[T1 >: AnyRef, T2 >: AnyRef, T3 >: AnyRef, T4 >: AnyRef, R](function: (T1, T2, T3, T4) => R): JFunction[Array[AnyRef], R] = (arr: Array[AnyRef]) => function.apply(arr(0), arr(1), arr(2), arr(3))
//
//  // Does not exist in the java world (without @FunctionalInterface), so follow convention of reactor by converting to function of Array[AnyRef]
//  def asJavaFn5[T1 >: AnyRef, T2 >: AnyRef, T3 >: AnyRef, T4 >: AnyRef, T5 >: AnyRef, R](function: (T1, T2, T3, T4, T5) => R): JFunction[Array[AnyRef], R] = (arr: Array[AnyRef]) => function.apply(arr(0), arr(1), arr(2), arr(3), arr(4))
//
//  // Does not exist in the java world (without @FunctionalInterface), so follow convention of reactor by converting to function of Array[AnyRef]
//  def asJavaFn6[T1 >: AnyRef, T2 >: AnyRef, T3 >: AnyRef, T4 >: AnyRef, T5 >: AnyRef, T6 >: AnyRef, R](function: (T1, T2, T3, T4, T5, T6) => R): JFunction[Array[AnyRef], R] = (arr: Array[AnyRef]) => function.apply(arr(0), arr(1), arr(2), arr(3), arr(4), arr(5))

}
