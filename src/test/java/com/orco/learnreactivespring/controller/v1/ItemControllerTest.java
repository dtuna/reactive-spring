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
    public void TestGetAllItems() {
        webTestClient.get().uri(ITEM_END_POINT_v1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5);

    }

    @Test
    public void TestGetAllItems_approach2() {
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
    public void TestGetAllItems_approach3() {
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
    public void TestGetItem() {
        webTestClient.get().uri(ITEM_END_POINT_v1.concat("/{id}"), "ABCD")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 260.0);

    }

    @Test
    public void TestGetItem_notFound() {
        webTestClient.get().uri(ITEM_END_POINT_v1.concat("/{id}"), "A")
                .exchange()
                .expectStatus().isNotFound();

    }

}