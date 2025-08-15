package com.codebase.orderservice.gateway;

public interface InventoryGateway {
    boolean tryDecrement(String itemId, int qty); // 409 -> false
    void increment(String itemId, int qty);
}
