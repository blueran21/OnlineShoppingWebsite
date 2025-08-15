package com.codebase.itemservice.service;

import com.codebase.itemservice.model.Item;
import com.codebase.itemservice.repository.ItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ItemServiceTest.class);

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;


    @BeforeAll
    static void setUp() {
        logger.info("Start Testing");
    }

    @Test
    public void testFindAll() {
        Item i1 = new Item(
                "Apple iphone 15",
                "The latest Apple smartphone with A17 chip",
                List.of("https://example.com/images/iphone15-front.jpg"),
                "111111111111",
                999.99
        );
        Item i2 = new Item(
                "Samsung Galaxy S24",
                "Flagship Android smartphone by Samsung",
                List.of("https://example.com/images/galaxy-s24.jpg"),
                "222222222222",
                899.99
        );

        Mockito.when(itemRepository.findAll()).thenReturn(List.of(i1, i2));

        List<Item> items = itemService.findAll();
        Assertions.assertEquals(2, items.size());
        Assertions.assertEquals(i1, items.get(0));
        Assertions.assertEquals(i2, items.get(1));
        logger.info("End testFindAll Testing");
    }

    @Test
    public void testFindById() {
        Item i1 = new Item(
                "Apple iphone 15",
                "The latest Apple smartphone with A17 chip",
                List.of("https://example.com/images/iphone15-front.jpg"),
                "111111111111",
                999.99
        );
        i1.setId("L1");

        Mockito.when(itemRepository.findById("L1")).thenReturn(Optional.of(i1));

        Optional<Item> item = itemService.findById("L1");
        Assertions.assertTrue(item.isPresent());
        Assertions.assertEquals(i1.getId(), item.get().getId());
        Assertions.assertEquals(i1.getName(), item.get().getName());
        Assertions.assertEquals(i1.getDescription(), item.get().getDescription());
        Assertions.assertEquals(i1.getPictureUrls(), item.get().getPictureUrls());
        Assertions.assertEquals(i1.getUpc(), item.get().getUpc());
        Assertions.assertEquals(i1.getPrice(), item.get().getPrice());
        logger.info("End testFindById Testing");
    }

    @Test
    public void testCreate() {
        Item i1 = new Item(
                "Apple iphone 15",
                "The latest Apple smartphone with A17 chip",
                List.of("https://example.com/images/iphone15-front.jpg"),
                "111111111111",
                999.99
        );
        i1.setId("L1");
        Mockito.when(itemRepository.save(i1)).thenReturn(i1);

        Item toSave = itemService.create(i1);
        Assertions.assertEquals(i1.getId(), toSave.getId());
        Assertions.assertEquals(i1.getName(), toSave.getName());
        Assertions.assertEquals(i1.getDescription(), toSave.getDescription());
        Assertions.assertEquals(i1.getPictureUrls(), toSave.getPictureUrls());
        Assertions.assertEquals(i1.getUpc(), toSave.getUpc());
        Assertions.assertEquals(i1.getPrice(), toSave.getPrice());

        Mockito.verify(itemRepository, Mockito.times(1)).save(i1);
        logger.info("End testCreate Testing");
    }

    @Test
    public void testUpdate() {
        Item i1 = new Item(
                "Apple iphone 15",
                "The latest Apple smartphone with A17 chip",
                List.of("https://example.com/images/iphone15-front.jpg"),
                "111111111111",
                999.99
        );
        i1.setId("L1");

        Mockito.when(itemRepository.save(i1)).thenReturn(i1);

        Item toUpdate =  itemService.update(i1);
        Assertions.assertEquals(i1.getId(), toUpdate.getId());
        Assertions.assertEquals(i1.getName(), toUpdate.getName());
        Assertions.assertEquals(i1.getDescription(), toUpdate.getDescription());
        Assertions.assertEquals(i1.getPictureUrls(), toUpdate.getPictureUrls());
        Assertions.assertEquals(i1.getUpc(), toUpdate.getUpc());
        Assertions.assertEquals(i1.getPrice(), toUpdate.getPrice());
        logger.info("End testUpdate Testing");

    }

    @Test
    public void testDelete() {
        Item i1 = new Item(
                "Apple iphone 15",
                "The latest Apple smartphone with A17 chip",
                List.of("https://example.com/images/iphone15-front.jpg"),
                "111111111111",
                999.99
        );
        i1.setId("L1");

        itemService.delete("L1");

        Mockito.verify(itemRepository, Mockito.times(1)).deleteById("L1");
        logger.info("End testDelete Testing");
    }

}
