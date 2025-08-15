package com.codebase.orderservice.dto;

import com.codebase.orderservice.model.OrderStatus;

import java.time.Instant;
import java.util.List;

/**
 * Response object returned to clients when querying or creating orders.
 */
public class OrderResponse {
    private String orderId;
    private String userId;
    private List<OrderItemResponse> items;
    private double totalPrice;
    private OrderStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public OrderResponse() {}

    public OrderResponse(String orderId, String userId, List<OrderItemResponse> items,
                         double totalPrice, OrderStatus status, Instant createdAt, Instant updatedAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters...
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

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
