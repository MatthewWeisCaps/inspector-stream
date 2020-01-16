package core;

import org.reactivestreams.Publisher;
import reactor.core.publisher.ParallelFlux;

class VarargsHelper {

  static <T> ParallelFlux<T> parallelFlux_fromSingleHelper(Publisher<? extends T> source) {
    return ParallelFlux.from(source);
  }


}
