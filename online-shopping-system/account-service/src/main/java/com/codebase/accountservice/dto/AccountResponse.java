package com.codebase.accountservice.dto;

import java.time.Instant;
import java.util.UUID;

public class AccountResponse {
    public UUID id;
    public String email;
    public String name;
    public String shippingAddress;
    public String billingAddress;
    public String paymentMethod;
    public Instant createdAt;
    public Instant updatedAt;

    public AccountResponse(UUID id, String email, String name, String shippingAddress,
                           String billingAddress, String paymentMethod,
                           Instant createdAt, Instant updatedAt) {
        this.id = id; this.email = email; this.name = name;
        this.shippingAddress = shippingAddress; this.billingAddress = billingAddress;
        this.paymentMethod = paymentMethod; this.createdAt = createdAt; this.updatedAt = updatedAt;
    }
}
