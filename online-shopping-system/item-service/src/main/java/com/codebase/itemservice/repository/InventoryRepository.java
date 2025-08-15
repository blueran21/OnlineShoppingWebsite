package com.codebase.itemservice.repository;

import com.codebase.itemservice.model.Inventory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository for {@link Inventory} documents. Provides methods to
 * lookup inventory by the associated item ID. Extending
 * MongoRepository exposes standard CRUD operations.
 */
public interface InventoryRepository extends MongoRepository<Inventory, String> {
    Optional<Inventory> findByItemId(String itemId);
    void deleteByItemId(String itemId);
}