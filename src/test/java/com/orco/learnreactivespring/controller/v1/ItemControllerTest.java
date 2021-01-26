package com.orco.learnreactivespring.controller.v1;

import com.orco.learnreactivespring.document.Item;
import com.orco.learnreactivespring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.orco.learnreactivespring.constants.ItemConstants.ITEM_END_POINT_v1;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
@Slf4j
@ActiveProfiles("test")
class ItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;

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
                .doOnNext(item -> log.info(item.toString()))
                .blockLast();
    }

    @Test
    public void testGetAllItems() {
        webTestClient.get().uri(ITEM_END_POINT_v1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5);

    }

    @Test
    public void testGetAllItems_approach2() {
        webTestClient.get().uri(ITEM_END_POINT_v1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5)
                .consumeWith(response -> {
                    List<Item> items = response.getResponseBody();
                    Objects.requireNonNull(items)
                            .forEach(item -> assertNotNull(item.getId()));
                });

    }

    @Test
    public void testGetAllItems_approach3() {
        Flux<Item> fluxItems = webTestClient.get().uri(ITEM_END_POINT_v1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class).getResponseBody();

        StepVerifier.create(fluxItems.log("Value from network"))
                .expectNextCount(5)
                .verifyComplete();

    }

    @Test
    public void testGetItem() {
        webTestClient.get().uri(ITEM_END_POINT_v1.concat("/{id}"), "ABCD")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 260.0);

    }

    @Test
    public void testGetItem_notFound() {
        webTestClient.get().uri(ITEM_END_POINT_v1.concat("/{id}"), "A")
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    public void testCreateItem() {
        Item item = new Item(null, "Iphone 12", 999.99);
        webTestClient.post().uri(ITEM_END_POINT_v1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Iphone 12")
                .jsonPath("$.price").isEqualTo(999.99);

    }

    @Test
    public void testDeleteItem() {
        webTestClient.delete().uri(ITEM_END_POINT_v1.concat("/{id}"), "ABCD")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);

    }

    @Test
    public void testupdateItem() {
//        Item item = new Item("ABCD", "Sony Headphone", 135.99);
        double newPrice = 135.99;
        Item item = new Item(null, "Sony Headphone", newPrice);

        webTestClient.put().uri(ITEM_END_POINT_v1.concat("/{id}"), "ABCD")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price").isEqualTo(newPrice);

    }

    @Test
    public void testupdateItem_notFound() {
//        Item item = new Item("ABCD", "Sony Headphone", 135.99);
        double newPrice = 135.99;
        Item item = new Item(null, "Sony Headphone", newPrice);

        webTestClient.put().uri(ITEM_END_POINT_v1.concat("/{id}"), "A")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();

    }

}