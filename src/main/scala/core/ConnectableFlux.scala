package core

import reactor.core.Disposable
import reactor.core.publisher.{ConnectableFlux => JConnectableFlux}
import reactor.core.scheduler.Scheduler

import scala.concurrent.duration.FiniteDuration

class ConnectableFlux[T](private val connectableDelegate: JConnectableFlux[T]) extends Flux[T] {

  final def autoConnect(): Flux[T] = Flux.from(connectableDelegate.autoConnect())
  final def autoConnect(minSubscribers: Int): Flux[T] = Flux.from(connectableDelegate.autoConnect(minSubscribers))
  final def autoConnect(minSubscribers: Int, cancelSupport: Disposable => Unit): Flux[T] = Flux.from(connectableDelegate.autoConnect(minSubscribers))

  final def connect(): Disposable = connectableDelegate.connect()
  final def connect(cancelSupport: Disposable => Unit): Unit = connectableDelegate.connect(asJavaConsumer(cancelSupport))

  // todo want to override this?
  final override def hide(): ConnectableFlux[T] = new ConnectableFlux[T](connectableDelegate.hide())

  final def refCount(): Flux[T] = Flux.from(connectableDelegate.refCount())
  final def refCount(minSubscribers: Int): Flux[T] = Flux.from(connectableDelegate.refCount(minSubscribers))
  final def refCount(minSubscribers: Int, gracePeriod: FiniteDuration): Flux[T] = Flux.from(connectableDelegate.refCount(minSubscribers, asJavaDuration(gracePeriod)))
  final def refCount(minSubscribers: Int, gracePeriod: FiniteDuration, scheduler: Scheduler): Flux[T] = Flux.from(connectableDelegate.refCount(minSubscribers, asJavaDuration(gracePeriod), scheduler))

  override private[core] val delegate = connectableDelegate

}
