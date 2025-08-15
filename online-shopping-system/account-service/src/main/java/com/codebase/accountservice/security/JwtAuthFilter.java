package com.codebase.accountservice.security;

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
                Claims claims = jwt.parseClaims(token); // 需要 JwtUtil 提供 parseClaims(...)
                String subject = claims.getSubject();   // 你的用户ID（login 时作为 sub 写入）

                if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    Collection<? extends GrantedAuthority> authorities = extractAuthorities(claims);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(subject, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception ignored) {
                // token 无效/过期/签名不对 —— 保持未认证，让后面的授权规则按未登录处理（返回 401）
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
            // 支持逗号分隔的字符串
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
