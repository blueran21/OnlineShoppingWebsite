package com.codebase.itemservice.service;

import com.codebase.itemservice.exception.NotFoundException;
import com.codebase.itemservice.model.Inventory;
import com.codebase.itemservice.repository.InventoryRepository;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.function.Supplier;


/**
 * Service for managing inventory levels. The inventory is stored in
 * MongoDB along with items. Each inventory record maps to a
 * particular item by its id and stores the available quantity.
 */
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final MongoTemplate mongoTemplate;

    public InventoryService(InventoryRepository inventoryRepository, MongoTemplate mongoTemplate) {
        this.inventoryRepository = inventoryRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public Inventory createInventory(String itemId, int quantity) {
        Inventory inventory = new Inventory(itemId, quantity);
        return inventoryRepository.save(inventory);
    }

    /**
     * Look up the inventory for a given item. If no record exists,
     * returns an empty optional.
     *
     * @param itemId the id of the item
     * @return optional inventory
     */
    public Optional<Inventory> getInventoryForItem(String itemId) {
        return inventoryRepository.findByItemId(itemId);
    }

    /**
     * Update the inventory quantity for a given item. If the
     * inventory record does not exist, it will be created.
     *
     * @param itemId   the id of the item
     * @param quantity the new quantity
     * @return updated inventory
     */
    @Transactional
    public Inventory updateInventory(String itemId, int quantity) {
        Inventory inventory = inventoryRepository.findByItemId(itemId)
                .orElseThrow(() -> new NotFoundException("Inventory not found for itemId: " + itemId));
        inventory.setQuantity(quantity);
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public void deleteByItemId(String itemId) {
        inventoryRepository.deleteByItemId(itemId);
    }

    /**
     * Atomic decreasement
     * Return true if decrease successfully, false if not enough inventory
     */
    @Transactional
    public boolean decrementIfEnough(String itemId, int req) {
        Query q = new Query(Criteria.where("itemId").is(itemId).and("quantity").gte(req));
        Update u = new Update().inc("quantity", -req);
        UpdateResult r = mongoTemplate.updateFirst(q, u, Inventory.class);
        return r.getModifiedCount() == 1;
    }

    // increment：if not exist, return exception
    @Transactional
    public int increment(String itemId, int qty) {
        Inventory inv = inventoryRepository.findByItemId(itemId)
                .orElseThrow(() -> new NotFoundException("Inventory not found for itemId: " + itemId));
        Query q = new Query(Criteria.where("itemId").is(itemId));
        Update u = new Update().inc("quantity", qty);
        mongoTemplate.updateFirst(q, u, Inventory.class);
        return inv.getQuantity() + qty;
    }
}