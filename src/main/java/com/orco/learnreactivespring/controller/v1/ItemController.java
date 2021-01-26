package com.orco.learnreactivespring.controller.v1;

import com.orco.learnreactivespring.document.Item;
import com.orco.learnreactivespring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static com.orco.learnreactivespring.constants.ItemConstants.ITEM_END_POINT_v1;

@RestController
@Slf4j
public class ItemController {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @GetMapping(value = ITEM_END_POINT_v1
//            , produces = {MediaType.APPLICATION_STREAM_JSON_VALUE}
            )
    private Flux<Item> getAllItems() {
        return itemReactiveRepository.findAll();
    }
}
