package com.orco.learnreactivespring.initialize;

import com.orco.learnreactivespring.document.Item;
import com.orco.learnreactivespring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@Profile("test")
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @Override
    public void run(String... args) throws Exception {

        initialDataSetup();
    }

    private List<Item> itemList = Arrays.asList(
            new Item(null, "Samsung TV", 500.0),
            new Item(null, "LG TV", 450.0),
            new Item(null, "Apple TV", 200.0),
            new Item(null, "Apple Watch", 350.0),
            new Item("ABCD", "Sony Headphone", 260.0)
    );

    private void initialDataSetup() {
        itemReactiveRepository
                .deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(item->log.info(item.toString()));
    }
}
