package com.codebase.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a single item in the incoming order request.
 */
public class OrderItemRequest {

    @NotBlank(message = "Item ID cannot be blank")
    private String itemId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    public OrderItemRequest() {
    }

    public OrderItemRequest(String itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
