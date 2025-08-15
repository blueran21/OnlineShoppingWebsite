package com.codebase.accountservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AccountCreateRequest {
    @Email @NotBlank public String email;
    @NotBlank public String name;
    @NotBlank @Size(min = 6, max = 100) public String password; // plain in request
    public String shippingAddress;
    public String billingAddress;
    public String paymentMethod;
}
