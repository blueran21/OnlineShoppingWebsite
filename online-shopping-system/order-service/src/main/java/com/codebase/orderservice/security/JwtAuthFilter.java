package com.codebase.orderservice.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwt;

    public JwtAuthFilter(JwtUtil jwt) {
        this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {

        String header = req.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwt.parseClaims(token);
                String subject = claims.getSubject();   // account-service 的 sub（用户ID字符串）

                if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    Collection<? extends GrantedAuthority> authorities = extractAuthorities(claims);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(subject, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception ignored) {

            }
        }
        chain.doFilter(req, res);
    }

    @SuppressWarnings("unchecked")
    private Collection<? extends GrantedAuthority> extractAuthorities(Claims claims) {
        Object rolesObj = claims.get("roles");
        List<GrantedAuthority> auths = new ArrayList<>();

        if (rolesObj instanceof List<?> list) {
            for (Object r : list) {
                if (r != null) auths.add(new SimpleGrantedAuthority(prefixRole(r.toString())));
            }
        } else if (rolesObj instanceof String s) {
            for (String r : s.split(",")) {
                if (!r.isBlank()) auths.add(new SimpleGrantedAuthority(prefixRole(r.trim())));
            }
        }
        return auths;
    }

    private String prefixRole(String r) {
        return r.startsWith("ROLE_") ? r : "ROLE_" + r;
    }
}
