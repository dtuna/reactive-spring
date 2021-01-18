package com.orco.learnreactivespring.fluxandmonoplayground;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoFilterTest {

    String names[] = new String[] { "Adam", "Anna", "Jack", "Jenny" };
    List<String> namesList = Arrays.asList(names);

    @Test
    public void filterTest() {
        Flux<String> namesFlux = Flux.fromIterable(namesList)
                .filter(s -> s.startsWith("A"))
                .log();
        StepVerifier.create(namesFlux)
                .expectNext("Adam", "Anna")
                .verifyComplete();
    }

    @Test
    public void filterTestLength() {
        Flux<String> namesFlux = Flux.fromIterable(namesList)
                .filter(s -> s.length() > 4)
                .log();
        StepVerifier.create(namesFlux)
                .expectNext("Jenny")
                .verifyComplete();
    }

}
