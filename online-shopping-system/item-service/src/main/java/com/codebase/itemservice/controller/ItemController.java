package com.codebase.itemservice.controller;

import com.codebase.itemservice.exception.ConflictException;
import com.codebase.itemservice.exception.NotFoundException;
import com.codebase.itemservice.model.Inventory;
import com.codebase.itemservice.model.Item;
import com.codebase.itemservice.service.InventoryService;
import com.codebase.itemservice.service.ItemService;
import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final InventoryService inventoryService;

    public ItemController(ItemService itemService, InventoryService inventoryService) {
        this.itemService = itemService;
        this.inventoryService = inventoryService;
    }

    // Check all times only for local test!
    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return  ResponseEntity.ok(itemService.findAll());
    }

    // Get an item by id
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItem(@PathVariable String id) {
        return itemService.findById(id).map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + id));
    }

    // Create a new item
    @PostMapping
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item item) {
        Item created = itemService.create(item);
        inventoryService.createInventory(created.getId(), 0);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Update an existing item
    @PostMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable String id, @Valid @RequestBody Item item) {
        Item updated = itemService.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + id));


        if (item.getDescription() != null) {
            updated.setDescription(item.getDescription());
        }
        if (item.getPictureUrls() != null) {
            updated.setPictureUrls(item.getPictureUrls());
        }
        if (item.getUpc() != null) {
            updated.setUpc(item.getUpc());
        }
        updated.setName(item.getName());
        updated.setPrice(item.getPrice());

        Item newUpdated = itemService.update(updated);
        return ResponseEntity.ok(newUpdated);
    }

    // Delete an item by id.
    @DeleteMapping("/{id}")
    public ResponseEntity<Item> deleteItem(@PathVariable String id) {
        if (itemService.findById(id).isEmpty()) {
            throw new NotFoundException("Item not found with id: " + id);
        }
        inventoryService.deleteByItemId(id);
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Retrieve the inventory quantity for an item.
    @GetMapping("/{id}/inventory")
    public ResponseEntity<Integer> getInventory(@PathVariable String id) {
        if (itemService.findById(id).isEmpty()) {
            throw new NotFoundException("Item not found with id: " + id);
        }
        Inventory inventory = inventoryService.getInventoryForItem(id)
                .orElseThrow(() -> new NotFoundException("Inventory not found for item ID: " + id));
        return ResponseEntity.ok(inventory.getQuantity());
    }

    /**
     * Update the inventory quantity for an item. Accepts a simple
     * integer body specifying the new quantity. A minimum of zero is
     * enforced.
     */
    @PutMapping("/{id}/inventory")
    public ResponseEntity<Inventory> updateInventory(
            @PathVariable String id,
            @RequestParam("quantity") @Min(0) int quantity) {
        Inventory updated = inventoryService.updateInventory(id, quantity);
        return ResponseEntity.ok(updated);
    }


    // 原子扣减库存（库存不足返回 409 Conflict）
    @PostMapping("/{id}/inventory/decrement")
    public ResponseEntity<Integer> decrementInventory(
            @PathVariable String id,
            @RequestParam("quantity") @Min(1) int quantity) {
        boolean ok = inventoryService.decrementIfEnough(id, quantity);
        if (!ok) {
            throw new ConflictException("Item not enough");
        }
        int newQty = inventoryService.getInventoryForItem(id).map(Inventory::getQuantity).orElse(0);
        return ResponseEntity.ok(newQty);
    }

    // 原子增加库存（用于取消订单时补回）
    @PostMapping("/{id}/inventory/increment")
    public ResponseEntity<Integer> incrementInventory(
            @PathVariable String id,
            @RequestParam("quantity") @Min(1) int quantity) {
        int newQty = inventoryService.increment(id, quantity);
        return ResponseEntity.ok(newQty);
    }


}
