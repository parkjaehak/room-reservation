package com.room_reservation.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TokenAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
            RequestUser reqUser = parseToken(auth);
            if (reqUser != null) {
                RequestUserHolder.set(reqUser);
            }
            filterChain.doFilter(request, response);
        } finally {
            RequestUserHolder.clear();
        }
    }

    private RequestUser parseToken(String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return null;
        }
        String token = authHeader.trim();
        if ("admin-token".equals(token)) {
            return new RequestUser(Role.ADMIN, null);
        }
        if (token.startsWith("user-token-")) {
            try {
                Long userId = Long.parseLong(token.substring("user-token-".length()));
                return new RequestUser(Role.USER, userId);
            } catch (NumberFormatException ignore) {
                return null;
            }
        }
        return null;
    }
}


