package com.codebase.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client to call payment-service.
 */
@FeignClient(name = "payment-service", url = "${payment.service.url}")
public interface PaymentServiceClient {

    @PostMapping("/payments")
    PaymentResponse submitPayment(@RequestBody PaymentRequest request);

    record PaymentRequest(String orderId, String userId, Double amount) {}
    record PaymentResponse(String status) {}
}
