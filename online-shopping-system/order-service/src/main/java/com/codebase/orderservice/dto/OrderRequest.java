package com.codebase.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Request body for creating or updating an order.
 * Contains userId and a list of items to order.
 */
public class OrderRequest {

    @NotBlank
    private String userId;

    @NotEmpty
    private List<OrderItemRequest> items;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}

