package reactor.core;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.function.Function;

public class JFluxVarargs {


    @SafeVarargs
    public static <T> Flux<T> justVarargs(T... data) {
        return Flux.just(data);
    }

    @SafeVarargs
    public static <T> Flux<T> just(T... data) {
        return Flux.just(data);
    }




}
