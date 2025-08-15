package com.codebase.orderservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;

@Component
public class JwtUtil {
    private final Key key;
    private final String issuer;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.issuer}") String issuer) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.issuer = issuer;
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .requireIssuer(issuer)
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
