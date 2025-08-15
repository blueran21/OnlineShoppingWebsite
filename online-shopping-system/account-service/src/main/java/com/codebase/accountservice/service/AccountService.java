package com.codebase.accountservice.service;

import com.codebase.accountservice.dto.*;
import com.codebase.accountservice.model.Account;
import com.codebase.accountservice.repository.AccountRepository;
import com.codebase.accountservice.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountRepository repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public AccountService(AccountRepository repo, PasswordEncoder encoder, JwtUtil jwt) {
        this.repo = repo; this.encoder = encoder; this.jwt = jwt;
    }

    public AccountResponse register(AccountCreateRequest req) {
        if (repo.existsByEmail(req.email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        Account a = new Account();
        a.setEmail(req.email.trim().toLowerCase());
        a.setName(req.name);
        a.setPasswordHash(encoder.encode(req.password));
        a.setShippingAddress(req.shippingAddress);
        a.setBillingAddress(req.billingAddress);
        a.setPaymentMethod(req.paymentMethod);
        a = repo.save(a);
        return toResp(a);
    }

    public LoginResponse login(LoginRequest req) {
        var acc = repo.findByEmail(req.email.trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!encoder.matches(req.password, acc.getPasswordHash()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");

        Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("email", acc.getEmail());
        if (acc.getName() != null && !acc.getName().isBlank()) {
            claims.put("name", acc.getName());
        }
        claims.put("roles", java.util.List.of("USER"));

        String token = jwt.generate(
                acc.getId().toString(),  // sub = userId (UUID)
                claims
        );

        return new LoginResponse(token);
    }

    public AccountResponse get(UUID id) {
        return repo.findById(id).map(this::toResp)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }

    public AccountResponse update(UUID id, AccountUpdateRequest req) {
        var a = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        if (req.name != null) a.setName(req.name);
        if (req.shippingAddress != null) a.setShippingAddress(req.shippingAddress);
        if (req.billingAddress != null) a.setBillingAddress(req.billingAddress);
        if (req.paymentMethod != null) a.setPaymentMethod(req.paymentMethod);
        if (req.newPassword != null && !req.newPassword.isBlank())
            a.setPasswordHash(encoder.encode(req.newPassword));
        a = repo.save(a);
        return toResp(a);
    }

    private AccountResponse toResp(Account a) {
        return new AccountResponse(
                a.getId(), a.getEmail(), a.getName(),
                a.getShippingAddress(), a.getBillingAddress(), a.getPaymentMethod(),
                a.getCreatedAt(), a.getUpdatedAt()
        );
    }
}
