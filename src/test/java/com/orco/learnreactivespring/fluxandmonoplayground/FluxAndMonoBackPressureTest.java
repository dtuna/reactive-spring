package com.orco.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {

    @Test
    public void backPressureTest() {
        Flux<Integer> integerFlux = Flux.range(1, 10).log();

        StepVerifier.create(integerFlux).expectSubscription().thenRequest(1).expectNext(1).thenRequest(1).expectNext(2)
                .thenRequest(1).expectNext(3).thenCancel().verify();
    }

    @Test
    public void backPressure() {
        Flux<Integer> integerFlux = Flux.range(1, 10).log();

        integerFlux.subscribe(element -> System.out.println("Element is: " + element),
                e -> System.err.println("Exception is: " + e.getMessage()), () -> System.out.println("Done"),
                subscription -> subscription.request(2));

    }

    @Test
    public void customizeBackPressure() {
        Flux<Integer> integerFlux = Flux.range(1, 10).log();

        integerFlux.subscribeWith(new BaseSubscriber<Integer>() {

            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println("Value received is: " + value);
                if (value == 4) {
                    cancel();
                }
                super.hookOnNext(value);
            }     
        });

    }

    @Test
    public void backPressure_cancel() {
        Flux<Integer> integerFlux = Flux.range(1, 10).log();

        integerFlux.subscribe(element -> System.out.println("Element is: " + element)
                , e -> System.err.println("Exception is: " + e.getMessage())
                , () -> System.out.println("Done")
                , subscription -> subscription.cancel());

    }

}
