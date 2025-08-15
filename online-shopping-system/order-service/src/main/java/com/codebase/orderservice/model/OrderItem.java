package com.codebase.orderservice.model;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

/**
 * User defined type representing an item within an order. Storing
 * order items as a UDT in Cassandra allows us to persist structured
 * data in a single table. Each item contains an identifier,
 * quantity and the price at the time of order placement.
 */
@UserDefinedType("order_item")
public class OrderItem {

    // ⚠️ names EXACTLY match the UDT: itemid, quantity, unitprice
    private String itemid;
    private int quantity;
    private double unitprice;

    public OrderItem() {}

    public OrderItem(String itemId, int quantity, double unitPrice) {
        this.itemid = itemId;
        this.quantity = quantity;
        this.unitprice = unitPrice;
    }

    public String getItemId() { return itemid; }
    public void setItemId(String itemId) { this.itemid = itemId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitprice; }
    public void setUnitPrice(double unitPrice) { this.unitprice = unitPrice; }
}