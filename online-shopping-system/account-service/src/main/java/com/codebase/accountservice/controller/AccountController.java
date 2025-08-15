package com.codebase.accountservice.controller;

import com.codebase.accountservice.dto.AccountResponse;
import com.codebase.accountservice.dto.AccountUpdateRequest;
import com.codebase.accountservice.service.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {
    private final AccountService service;
    public AccountController(AccountService service) { this.service = service; }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> update(@PathVariable UUID id, @Valid @RequestBody AccountUpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }
}
