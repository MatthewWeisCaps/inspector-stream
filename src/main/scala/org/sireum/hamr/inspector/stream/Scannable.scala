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

import reactor.core.{Scannable => JScannable}

import scala.collection.JavaConverters._

trait Scannable {

  private[stream] def jscannable: JScannable

  def actuals: Stream[Scannable] = jscannable.actuals.iterator().asScala.toStream.map(scannable => new Scannable {
    override private[stream] def jscannable = scannable
  })

  def inners: Stream[Scannable] = jscannable.inners.iterator().asScala.toStream.map(scannable => new Scannable {
    override private[stream] def jscannable = scannable
  })

  def isScanAvailable: Boolean = jscannable.isScanAvailable

  def name: String = jscannable.name

  def parents: Stream[Scannable] = jscannable.parents.iterator().asScala.toStream.map(scannable => new Scannable {
    override private[stream] def jscannable = scannable
  })

  def scan[T](key: JScannable.Attr[T]): Option[T] = Option(jscannable.scan[T](key))

  def scanOrDefault[T](key: JScannable.Attr[T], defaultValue: T): T = jscannable.scanOrDefault(key, defaultValue)

  def scanUnsafe(key: JScannable.Attr[_]): AnyRef = Option(jscannable.scanUnsafe(key))

  def stepName: String = jscannable.stepName

  def steps: Stream[String] = jscannable.steps.iterator().asScala.toStream

  def tags: Stream[(String, String)] = jscannable.tags.iterator().asScala.toStream.map(JavaInterop.toScalaTuple2)

}

object Scannable {
  /**
    *
    *
    * Credit: added Scannable match case after reading reactor-scala-extension's implementation at:
    * https://github.com/reactor/reactor-scala-extensions/blob/master/src/main/scala/reactor/core/scala/Scannable.scala#L91
    * todo add their license + notice
    *
    * @param any
    * @return
    */
  protected[stream] def from(any: Option[AnyRef]): Scannable = any match {
    case None => new Scannable {
      override private[stream] def jscannable: JScannable = JScannable.from(null)
    }
    case Some(scannable: Scannable) => new Scannable {
      override private[stream] def jscannable: JScannable = JScannable.from(scannable.jscannable)
    }
    case Some(value) => new Scannable {
      override private[stream] def jscannable: JScannable = JScannable.from(value)
    }
  }

  protected[stream] type Attr[T] = JScannable.Attr[T]

}
