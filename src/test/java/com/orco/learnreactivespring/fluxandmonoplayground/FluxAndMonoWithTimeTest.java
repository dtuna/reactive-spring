package com.orco.learnreactivespring.fluxandmonoplayground;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoWithTimeTest {

    @Test
    public void infiniteSequenceTest() throws InterruptedException {
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceMapTest_WithDelay() throws InterruptedException {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .delayElements(Duration.ofSeconds(1))
                .map(l -> new Integer(l.intValue()))
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceMapTest() throws InterruptedException {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .map(l -> new Integer(l.intValue()))
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }
    
}
