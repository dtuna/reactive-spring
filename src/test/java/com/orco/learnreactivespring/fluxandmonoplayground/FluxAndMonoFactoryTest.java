package com.orco.learnreactivespring.fluxandmonoplayground;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoFactoryTest {

    String names[] = new String[] { "Adam", "Anna", "Jack", "Jenny" };
    List<String> namesList = Arrays.asList(names);

    @Test
    public void fluxUsingIterable() {
        Flux<String> namesFlux = Flux.fromIterable(namesList).log();
        StepVerifier.create(namesFlux).expectNextSequence(namesList).verifyComplete();
    }

    @Test
    public void fluxUsingArray() {
        Flux<String> namesFlux = Flux.fromArray(names).log();
        StepVerifier.create(namesFlux).expectNextSequence(namesList).verifyComplete();
    }

    @Test
    public void fluxUsingStream() {
        Flux<String> namesFlux = Flux.fromStream(namesList.stream()).log();
        StepVerifier.create(namesFlux).expectNext(names).verifyComplete();
    }

    @Test
    public void fluxUsingRange() {
        Flux<Integer> integerFlux = Flux.range(1, 5);
        StepVerifier.create(integerFlux.log())
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    public void monoUsingJustOrEmpty() {
        Mono<String> mono = Mono.justOrEmpty(null);
        StepVerifier.create(mono.log()).verifyComplete();
    }

    @Test
    public void monoUsingSupplier() {
        Supplier<String> stringSupplier = () -> "Adam";
        Mono<String> mono = Mono.fromSupplier(stringSupplier);
        StepVerifier.create(mono.log()).expectNext("Adam").verifyComplete();
    }
}
