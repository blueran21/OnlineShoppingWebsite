package com.codebase.itemservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents the inventory record for a specific item. Inventory
 * information lives alongside items in MongoDB, decoupling stock
 * levels from order records. Each inventory record references an
 * existing item via its itemId and tracks the number of units
 * remaining.
 */
@Document(collection = "inventory")
public class Inventory {

    @Id
    private String id;

    @NotBlank
    private String itemId;

    @NotNull
    private Integer quantity;

    public Inventory() {
    }

    public Inventory(String itemId, Integer quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
