package com.orco.learnreactivespring.fluxandmonoplayground;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

public class FluxAndMonoTransformTest {

    String names[] = new String[] { "Adam", "Anna", "Jack", "Jenny" };
    List<String> namesList = Arrays.asList(names);

    @Test
    public void transformUsingMapTest() {
        Flux<String> namesFlux = Flux.fromIterable(namesList).map(s -> s.toUpperCase()).log();

        StepVerifier.create(namesFlux).expectNext("ADAM", "ANNA", "JACK", "JENNY").verifyComplete();
    }

    @Test
    public void transformUsingMapTest_Length() {
        Flux<Integer> integerFlux = Flux.fromIterable(namesList).map(s -> s.length()).log();

        StepVerifier.create(integerFlux).expectNext(4, 4, 4, 5).verifyComplete();
    }

    @Test
    public void transformUsingMapTest_Length_Repeat() {
        Flux<Integer> integerFlux = Flux.fromIterable(namesList).map(s -> s.length()).repeat(1).log();

        StepVerifier.create(integerFlux).expectNext(4, 4, 4, 5, 4, 4, 4, 5).verifyComplete();
    }

    @Test
    public void transformUsingMapTest_Filter() {
        Flux<Integer> integerFlux = Flux.fromIterable(namesList).filter(s -> s.length() > 4).map(s -> s.length())
                .repeat(2).log();

        StepVerifier.create(integerFlux).expectNext(5, 5, 5).verifyComplete();
    }

    @Test
    public void transformUsingFlatMapTest() {
        Flux<String> namesFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) // A, B, C, D, E, F
                .flatMap(s -> {
                    return Flux.fromIterable(convertToList(s)); // A -> A, NewValue, B -> B, NewValue ...
                }) // db or external service call that returns a flux -> s -> Flux<String>
                .log();

        StepVerifier.create(namesFlux)
                .expectNextCount(12)
                .verifyComplete();    
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "NewValue");
    }
    
    @Test
    public void transformUsingFlatMapTest_UsingParallel() {        
        Flux<String> namesFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) // Flux<String>
                .window(2) // Flux<Flux<String>> -> (A, B), (C, D), (E, F)
                .flatMap(f -> f.map(this::convertToList).subscribeOn(Schedulers.parallel())) // Flux<List<String>>
                .flatMap(s -> Flux.fromIterable(s)) // Flux<String>
                .log();

        StepVerifier.create(namesFlux)
                .expectNextCount(12)
                .verifyComplete();    
    }
    
    @Test
    public void transformUsingFlatMapTest_UsingParallel_MaintainOrder() {        
        Flux<String> namesFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) // Flux<String>
                .window(2) // Flux<Flux<String>> -> (A, B), (C, D), (E, F)
                // .concatMap(f -> f.map(this::convertToList).subscribeOn(Schedulers.parallel())) // Flux<List<String>>
                .flatMapSequential(f -> f.map(this::convertToList).subscribeOn(Schedulers.parallel())) // Flux<List<String>>
                .flatMap(s -> Flux.fromIterable(s)) // Flux<String>
                .log();

        StepVerifier.create(namesFlux)
                .expectNextCount(12)
                .verifyComplete();    
    }
    
}
