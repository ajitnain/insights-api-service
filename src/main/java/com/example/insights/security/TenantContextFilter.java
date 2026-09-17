package com.example.insights.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/** Authenticates the bearer token and stores the caller. */
@Component
@Order(1)
@RequiredArgsConstructor
public class TenantContextFilter extends OncePerRequestFilter {

    private final DemoTokens demoTokens;
    private final ObjectMapper json;

    private static final String PREFIX = "Bearer ";

    // Keep the API docs open.
    private static final List<String> OPEN_PATHS = List.of("/v3/api-docs", "/swagger-ui");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return OPEN_PATHS.stream().anyMatch(request.getRequestURI()::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, jakarta.servlet.ServletException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(PREFIX)) {
            unauthorized(request, response, "missing bearer token");
            return;
        }

        Caller caller = demoTokens.caller(header.substring(PREFIX.length()).trim());
        if (caller == null) {
            unauthorized(request, response, "token not recognised");
            return;
        }

        try {
            TenantContext.set(caller);
            chain.doFilter(request, response);
        } finally {
            // Threads are reused, so clear the caller before the next request.
            TenantContext.clear();
        }
    }

    // The filter runs before controller advice, so write the same error shape here.
    private void unauthorized(HttpServletRequest request, HttpServletResponse response, String reason)
            throws IOException {

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Unauthorized");
        problem.setDetail(reason);
        problem.setInstance(URI.create(request.getRequestURI()));

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        json.writeValue(response.getWriter(), problem);
    }
}
