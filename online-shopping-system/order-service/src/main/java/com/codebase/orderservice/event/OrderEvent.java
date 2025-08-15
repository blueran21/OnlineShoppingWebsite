package com.codebase.orderservice.event;

import com.codebase.orderservice.model.OrderStatus;

import java.util.List;

/**
 * OrderEvent is sent via Kafka when a new order is created.
 * Other services can consume this to trigger workflows.
 */
public class OrderEvent {
    private String orderId;
    private String userId;
    private List<ItemLine> items;
    private double totalPrice;
    private OrderStatus status;

    public OrderEvent() {}

    public OrderEvent(String orderId, String userId, List<ItemLine> items, double totalPrice, OrderStatus status) {
        this.orderId = orderId;
        this.userId = userId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<ItemLine> getItems() {
        return items;
    }

    public void setItems(List<ItemLine> items) {
        this.items = items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * Represents an item inside the OrderEvent.
     */
    public static class ItemLine {
        private String itemId;
        private int quantity;

        public ItemLine() {}

        public ItemLine(String itemId, int quantity) {
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
}

