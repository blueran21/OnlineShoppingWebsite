package com.codebase.accountservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    private final Key key;
    private final String issuer;
    private final long expirationMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.expiration-minutes}") long minutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.issuer = issuer;
        this.expirationMs = minutes * 60_000;
    }

    public String generate(String subject, Map<String, Object> claims) {
        var now = new Date();
        var exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key)
                .compact();
    }

    public Jws<Claims> parseSigned(String token) {
        return Jwts.parserBuilder()
                .requireIssuer(issuer)
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .requireIssuer(issuer)
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isValid(String token) {
        try {
            parseSigned(token); // will throw if invalid/expired
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
