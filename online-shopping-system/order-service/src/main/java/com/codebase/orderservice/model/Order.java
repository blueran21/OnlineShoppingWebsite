package com.codebase.orderservice.model;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.cassandra.core.mapping.CassandraType.Name.LIST;
import static org.springframework.data.cassandra.core.mapping.CassandraType.Name.UDT;

/**
 * Cassandra entity representing an order. Orders are partitioned by
 * order_id which makes lookups by id efficient. A list of order
 * items is stored using a user defined type. Timestamps record when
 * the order was created and last updated. Status tracks the order
 * lifecycle.
 */
@Table("orders")
public class Order {

    @PrimaryKey
    @Column("order_id")
    private UUID orderId;

    @Column("user_id")
    private String userId;

    @Column("items")
    @Frozen
//    @CassandraType(type = LIST, typeArguments = UDT, userTypeName = "order_item")
    private List<OrderItem> items;

    @Column("total_price")
    private double totalPrice;

    @Column("status")
    private OrderStatus status;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    public Order() {
    }

    public Order(UUID orderId, String userId, List<OrderItem> items, double totalPrice, OrderStatus status, Instant createdAt, Instant updatedAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
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