package reactor.core;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.function.Function;

public class JFluxVarargs {


    @SafeVarargs
    public static <T> Flux<T> justVarargs(T... data) {
        return Flux.just(data);
    }

    public static <T> Flux<T> just(T data) {
        return Flux.just(data);
    }

    @SafeVarargs
    public static <I> Flux<I> firstVarargs(Publisher<? extends I> ... sources) {
        return Flux.first(sources);
    }

    public static <I> Flux<I> first(Iterable<? extends Publisher<? extends I>> sources) {
        return Flux.first(sources);
    }




}
