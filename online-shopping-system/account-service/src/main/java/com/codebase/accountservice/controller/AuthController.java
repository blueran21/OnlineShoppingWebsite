package com.codebase.accountservice.controller;

import com.codebase.accountservice.dto.*;
import com.codebase.accountservice.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AccountService service;
    public AuthController(AccountService service) { this.service = service; }

    @PostMapping("/register")
    public ResponseEntity<AccountResponse> register(@Valid @RequestBody AccountCreateRequest req) {
        return ResponseEntity.ok(service.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(service.login(req));
    }
}
