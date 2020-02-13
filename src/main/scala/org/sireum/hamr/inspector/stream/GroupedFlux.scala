package org.sireum.hamr.inspector.stream

import reactor.core.publisher.{GroupedFlux => JGroupedFlux}

final class GroupedFlux[K, V](protected val groupedDelegate: JGroupedFlux[K, V]) extends Flux[V] {

  override private[stream] val delegate = groupedDelegate

  def key(): K = delegate.key()

}

object GroupedFlux {
  def apply[K, V](delegate: JGroupedFlux[K, V]) = new GroupedFlux[K, V](delegate)
}