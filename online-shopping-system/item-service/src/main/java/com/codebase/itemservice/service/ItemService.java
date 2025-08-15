package com.codebase.itemservice.service;

import com.codebase.itemservice.model.Item;
import com.codebase.itemservice.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

/**
 * Service layer for item operations. All business logic related to
 * creating, updating, retrieving and deleting items lives here.
 */
@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * Retrieve all items in the catalogue.
     *
     * @return list of all items
     */
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    /**
     * Find an item by its identifier.
     *
     * @param id item id
     * @return optional of item
     */
    public Optional<Item> findById(String id) {
        return itemRepository.findById(id);
    }

    /**
     * Persist a new item in the catalogue.
     *
     * @param item item to create
     * @return created item
     */
    @Transactional
    public Item create(Item item) {
        return itemRepository.save(item);
    }

    /**
     * Update an existing item. The caller must ensure the id is set on
     * the passed in item.
     *
     * @param item updated item
     * @return updated record
     */
    @Transactional
    public Item update(Item item) {
        return itemRepository.save(item);
    }

    /**
     * Delete an item from the catalogue.
     *
     * @param id item id
     */
    @Transactional
    public void delete(String id) {
        itemRepository.deleteById(id);
    }

}
