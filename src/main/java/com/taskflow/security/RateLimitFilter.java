package com.taskflow.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple fixed-window rate limiter for the auth endpoints, keyed by client IP.
 * Not distributed-safe; fine for a single instance, would need a shared store (Redis) to scale out.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_WINDOW = 10;
    private static final long WINDOW_MILLIS = 60_000;

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (!request.getRequestURI().startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientKey = request.getRemoteAddr();
        long now = Instant.now().toEpochMilli();

        Window window = windows.compute(clientKey, (key, existing) -> {
            if (existing == null || now - existing.windowStart > WINDOW_MILLIS) {
                return new Window(now);
            }
            return existing;
        });

        if (window.count.incrementAndGet() > MAX_REQUESTS_PER_WINDOW) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Too many requests, please try again later\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static final class Window {
        private final long windowStart;
        private final AtomicInteger count = new AtomicInteger(0);

        private Window(long windowStart) {
            this.windowStart = windowStart;
        }
    }
}
