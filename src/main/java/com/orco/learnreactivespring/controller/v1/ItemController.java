package com.orco.learnreactivespring.controller.v1;

import com.orco.learnreactivespring.document.Item;
import com.orco.learnreactivespring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.orco.learnreactivespring.constants.ItemConstants.ITEM_END_POINT_v1;

@RestController
@Slf4j
public class ItemController {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @GetMapping(value = ITEM_END_POINT_v1)
    private Flux<Item> getAllItems() {
        return itemReactiveRepository.findAll();
    }

    @GetMapping(value = ITEM_END_POINT_v1 + "/{id}")
    private Mono<ResponseEntity<Item>> getItem(@PathVariable String id) {
        return itemReactiveRepository.findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(value = ITEM_END_POINT_v1)
    @ResponseStatus(HttpStatus.CREATED)
    private Mono<Item> createItem(@RequestBody Item item) {
        return itemReactiveRepository.save(item);
    }
}
