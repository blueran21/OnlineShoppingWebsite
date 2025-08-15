package com.codebase.itemservice.repository;

import com.codebase.itemservice.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Spring Data repository for managing {@link Item} documents. This
 * interface leverages MongoRepository to provide CRUD operations out
 * of the box. Additional query methods can be declared here when
 * needed.
 */
public interface ItemRepository extends MongoRepository<Item, String> {
    // Additional derived query methods can be added here
}
