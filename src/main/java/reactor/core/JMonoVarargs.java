package reactor.core;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class JMonoVarargs {


//    @SafeVarargs
//    public static <T> Flux<T> justVarargs(T... data) {
//        return Flux.just(data);
//    }
//
//    public static <T> Flux<T> just(T data) {
//        return Flux.just(data);
//    }

    @SafeVarargs
    public static <T> Mono<T> firstVarargs(Mono<? extends T>... monos) {
        return Mono.first(monos);
    }

    public static <T> Mono<T> first(Iterable<? extends Mono<? extends T>> monos) {
        return Mono.first(monos);
    }




}
