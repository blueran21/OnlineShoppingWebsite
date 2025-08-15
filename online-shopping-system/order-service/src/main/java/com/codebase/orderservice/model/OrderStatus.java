package com.codebase.orderservice.model;

/**
 * Enumeration of the possible order states. While orders are created
 * asynchronously, they transition through these states based on
 * payment and fulfilment events. Persisting the status allows us to
 * resume order handling after failures.
 */
public enum OrderStatus {
    CREATED,
    PAID,
    COMPLETED,
    CANCELLED
}