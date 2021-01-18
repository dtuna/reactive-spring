package com.orco.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

    @Test
    public void fluxTest() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                // .concatWith(Flux.error(new RuntimeException("Test error")))
                .concatWith(Flux.just("After Error"))
                .log();

        stringFlux
                .subscribe(System.out::println, 
                        (e) -> System.err.println("Exception is " + e), 
                        () -> System.out.println("Completed"));
    }

    @Test
    public void fluxTestElements_WithoutError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring").log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .verifyComplete();

    }

    @Test
    public void fluxTestElements_WithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Test error")))
                .log()
                ;

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                // .expectError(RuntimeException.class)
                .expectErrorMessage("Test error")
                .verify();
    }

    @Test
    public void fluxTestElementsCount_WithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Test error")))
                .log()
                ;

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .expectErrorMessage("Test error")
                .verify();
    }

    @Test
    public void fluxTestElements_WithErrorAlternateWay() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Test error")))
                .log()
                ;

        StepVerifier.create(stringFlux)
                .expectNext("Spring", "Spring Boot", "Reactive Spring")
                .expectErrorMessage("Test error")
                .verify();
    }

    @Test
    public void monoTest() {
            Mono<String> strnigMono =Mono.just("Spring");

            StepVerifier.create(strnigMono)
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void monoTest_WithError() {
            StepVerifier.create(Mono.error(new RuntimeException("Exception occurred")).log())
                .expectError(RuntimeException.class)
                .verify();
    }

}
