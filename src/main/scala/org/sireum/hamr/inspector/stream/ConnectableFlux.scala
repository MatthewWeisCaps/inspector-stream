/*
 * Copyright (c) 2020, Matthew Weis, Kansas State University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sireum.hamr.inspector.stream

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

  override private[stream] val delegate = connectableDelegate

}
