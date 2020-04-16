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
import reactor.core.publisher.{FluxSink => JFluxSink}
import reactor.util.context.Context

trait FluxSink[T] {

  type OverflowStrategy = JFluxSink.OverflowStrategy

  private[stream] def delegate: JFluxSink[T]

  def complete(): Unit = delegate.complete()

  def currentContext(): Context = delegate.currentContext()

  def error(e: Throwable): Unit = delegate.error(e)
  def next(t: T): FluxSink[T] = FluxSink.wrap[T](delegate.next(t))

  def requestedFromDownstream(): Long = delegate.requestedFromDownstream()

  def isCancelled: Boolean = delegate.isCancelled

  def onRequest(consumer: Long => Unit): FluxSink[T] = FluxSink.wrap[T](delegate.onRequest(JavaInterop.asJavaLongConsumer(consumer)))
  def onCancel(d: Disposable): FluxSink[T] = FluxSink.wrap[T](delegate.onCancel(d))
  def onDispose(d: Disposable): FluxSink[T] = FluxSink.wrap[T](delegate.onDispose(d))
}

object FluxSink {

  type OverflowStrategy = JFluxSink.OverflowStrategy

  def wrap[T](jFluxSink: JFluxSink[T]): FluxSink[T] = new FluxSink[T] {
    override private[stream] def delegate: JFluxSink[T] = jFluxSink
  }
}
