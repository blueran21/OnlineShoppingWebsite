package com.codebase.itemservice.service;

import com.codebase.itemservice.exception.NotFoundException;
import com.codebase.itemservice.model.Inventory;
import com.codebase.itemservice.repository.InventoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceTest.class);

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private InventoryService inventoryService;

    @BeforeAll
    static void setUp() {
        logger.info("Start Testing");
    }

    @Test
    public void testCreateInventory() {
        Inventory inventory = new Inventory("L1", 1);

        Mockito.when(inventoryRepository.save(Mockito.any(Inventory.class))).thenReturn(inventory);

        Inventory toSave = inventoryService.createInventory("L1", 1);
        Assertions.assertNotNull(toSave);

        Assertions.assertEquals("L1", toSave.getItemId());
        Assertions.assertEquals(1, toSave.getQuantity());

        Mockito.verify(inventoryRepository, Mockito.times(1)).save(Mockito.any(Inventory.class));
        Mockito.verify(inventoryRepository).save(argThat(inv -> "L1".equals(inv.getItemId()) && inv.getQuantity() == 1));

        logger.info("End testCreateInventory Testing");
    }

    @Test
    public void testGetInventoryForItem() {
        Inventory inventory = new Inventory();
        inventory.setItemId("L1");
        inventory.setQuantity(1);

        Mockito.when(inventoryRepository.findByItemId("L1")).thenReturn(Optional.of(inventory));

        Optional<Inventory> toFind = inventoryService.getInventoryForItem("L1");

        Assertions.assertTrue(toFind.isPresent());
        Assertions.assertEquals(inventory, toFind.get());
        Assertions.assertEquals(inventory.getQuantity(), toFind.get().getQuantity());
        logger.info("End testGetInventoryForItem Testing");
    }

    @Test
    public void testUpdateInventoryWithFound() {
        Inventory existing = new Inventory("L1", 5);

        Mockito.when(inventoryRepository.findByItemId("L1")).thenReturn(Optional.of(existing));
        Mockito.when(inventoryRepository.save(Mockito.any(Inventory.class))).thenAnswer(i -> i.getArgument(0));

        Inventory result = inventoryService.updateInventory("L1", 10);

        Assertions.assertNotNull(result);
        Assertions.assertSame(existing, result);
        Assertions.assertEquals("L1", result.getItemId());
        Assertions.assertEquals(10, result.getQuantity());

        ArgumentCaptor<Inventory> captor = ArgumentCaptor.forClass(Inventory.class);
        Mockito.verify(inventoryRepository, Mockito.times(1)).findByItemId("L1");
        Mockito.verify(inventoryRepository, Mockito.times(1)).save(captor.capture());
        Inventory capture = captor.getValue();
        Assertions.assertEquals("L1", capture.getItemId());
        Assertions.assertEquals(10, capture.getQuantity());

        Mockito.verifyNoMoreInteractions(inventoryRepository);

        logger.info("End testUpdateInventoryWithFound Testing");
    }

    @Test
    public void testUpdateInventoryWithNotFound() {
        Mockito.when(inventoryRepository.findByItemId(ArgumentMatchers.anyString()))
                .thenThrow(new NotFoundException("Inventory not found for itemId: " + "L1"));

        Assertions.assertThrows(NotFoundException.class, () -> inventoryService.updateInventory("L1", 10));
        Mockito.verify(inventoryRepository, Mockito.times(1)).findByItemId("L1");
        Mockito.verify(inventoryRepository, Mockito.never()).save(Mockito.any(Inventory.class));
        Mockito.verifyNoMoreInteractions(inventoryRepository);

        logger.info("End testUpdateInventoryWithNotFound Testing");
    }

    @Test
    public void testDeleteByItemId() {
        inventoryService.deleteByItemId("L1");
        Mockito.verify(inventoryRepository, Mockito.times(1)).deleteByItemId("L1");
        Mockito.verifyNoMoreInteractions(inventoryRepository);

        logger.info("End testDeleteByItemId Testing");
    }

}
