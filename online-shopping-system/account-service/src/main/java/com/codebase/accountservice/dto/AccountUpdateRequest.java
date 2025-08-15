package com.codebase.accountservice.dto;

import jakarta.validation.constraints.Size;

public class AccountUpdateRequest {
    public String name;
    public String shippingAddress;
    public String billingAddress;
    public String paymentMethod;
    @Size(min = 6, max = 100)
    public String newPassword; // optional
}
