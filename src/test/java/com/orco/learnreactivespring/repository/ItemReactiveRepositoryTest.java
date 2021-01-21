package com.orco.learnreactivespring.repository;

import com.orco.learnreactivespring.document.Item;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ItemReactiveRepositoryTest {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    private List<Item> itemList = Arrays.asList(
            new Item(null, "Samsung TV", 500.0),
            new Item(null, "LG TV", 450.0),
            new Item(null, "Apple TV", 200.0),
            new Item(null, "Apple Watch", 350.0),
            new Item("ABCD", "Sony Headphone", 260.0)
    );

    @BeforeEach
    void setUp() {
        itemReactiveRepository
                .deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    public void testFindAll() {
        StepVerifier.create(itemReactiveRepository.findAll().log())
                .expectSubscription()
                .expectNextCount(5L)
                .verifyComplete();
    }

    @Test
    public void testFindById() {
        StepVerifier.create(itemReactiveRepository.findById("ABCD").log("FindById"))
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Sony Headphone"))
                .verifyComplete();
    }

    @Test
    public void testFindByDescription() {
        StepVerifier.create(itemReactiveRepository.findByDescription("Sony Headphone").log())
                .expectSubscription()
                .expectNextMatches(item -> item.getId().equals("ABCD"))
                .verifyComplete();
    }

    @Test
    public void testSaveItem() {
        Item item = new Item(null, "Chrome Cast", 30.00);

        StepVerifier.create(itemReactiveRepository.save(item).log())
                .expectSubscription()
                .expectNextMatches(i -> StringUtils.isNotBlank(i.getId()))
                .verifyComplete();
    }

    @Test
    public void testUpdateItem() {

        Double newPrice = 480.00;
        StepVerifier
                .create(
                        itemReactiveRepository
                                .findByDescription("LG TV")
                                .map(item -> {
                                    item.setPrice(newPrice);
                                    return item;
                                })
                                .flatMap(itemReactiveRepository::save)
                                .log()
                )
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice().equals(newPrice))
                .verifyComplete();
    }

    @Test
    public void testDeleteById() {

        Mono<Void> deletedItem = itemReactiveRepository.findById("ABCD") // Mono<Item>
                .map(Item::getId) // get Id -> transform one type to another type
                .flatMap(itemReactiveRepository::deleteById);

        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log())
                .expectSubscription()
                .expectNextCount(4L)
                .verifyComplete();
    }

    @Test
    public void testDelete() {

        Mono<Void> deletedItem = itemReactiveRepository.findByDescription("LG TV") // Mono<Item>
                .flatMap(itemReactiveRepository::delete);

        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log())
                .expectSubscription()
                .expectNextCount(4L)
                .verifyComplete();
    }
}
