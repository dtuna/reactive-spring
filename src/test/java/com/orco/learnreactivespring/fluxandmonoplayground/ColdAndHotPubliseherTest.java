package com.orco.learnreactivespring.fluxandmonoplayground;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

public class ColdAndHotPubliseherTest {

    @Test
    public void coldPublisherTest() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F", "G", "H")
                .delayElements(Duration.ofSeconds(1));

        stringFlux.subscribe(s -> System.out.println("Subscriber 1: " + s)); // emits a value from beginning
        Thread.sleep(2000);

        stringFlux.subscribe(s -> System.out.println("Subscriber 2: " + s)); // emits a value from beginning
        Thread.sleep(4000);
    }

    @Test
    public void hotPublisherTest() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F", "G", "H")
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<String> connectableFlux = stringFlux.publish();

        connectableFlux.connect();
        connectableFlux.subscribe(s -> System.out.println("Subscriber 1: " + s)); 
        Thread.sleep(2000);

        connectableFlux.subscribe(s -> System.out.println("Subscriber 2: " + s)); // does not emits a value from beginning
        Thread.sleep(4000);
    }
    
}
