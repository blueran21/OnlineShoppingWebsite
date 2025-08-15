package com.codebase.orderservice.controller;

import com.codebase.orderservice.dto.OrderRequest;
import com.codebase.orderservice.dto.OrderResponse;
import com.codebase.orderservice.service.OrderAppService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderAppService appService;

    public OrderController(OrderAppService appService) {
        this.appService = appService;
    }

    // Create Order (Bond with current user)
    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request,
                                                Authentication auth) {
        String userId = (String) auth.getPrincipal(); // sub from JWT
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appService.create(request, userId));
    }

    // Get Order by ID（Only bond with current user）
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> get(@PathVariable("id") UUID id,
                                             Authentication auth) {
        String userId = (String) auth.getPrincipal();
        OrderResponse resp = appService.get(id, userId);
        return (resp == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(resp);
    }

    // Update Order（Only when has been created and bond with current user）
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> update(@PathVariable("id") UUID id,
                                                @Valid @RequestBody OrderRequest request,
                                                Authentication auth) {
        String userId = (String) auth.getPrincipal();
        return ResponseEntity.ok(appService.update(id, request, userId));
    }

    // Cancel Order（Bond with current user）
    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancel(@PathVariable("id") UUID id,
                                                Authentication auth) {
        String userId = (String) auth.getPrincipal();
        return ResponseEntity.ok(appService.cancel(id, userId));
    }

    // List all orders with current user. Bond with current user
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(Authentication auth) {
        String userId = (String) auth.getPrincipal();
        List<OrderResponse> resp = appService.list(userId);
        Map<String, Object> body = new HashMap<>();
        if (resp.isEmpty()) {
            body.put("message", "no orders");
            body.put("data", Collections.emptyList());
        } else {
            body.put("message", "success");
            body.put("data", resp);
        }
        return ResponseEntity.ok(body);
    }
}
