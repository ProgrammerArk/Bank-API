package com.eaglebank.eagle_bank_api.config;


import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Skip authentication for POST /v1/users (user registration)
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        if ("POST".equals(method) && requestURI.matches(".*/v1/users/?$")) {
            return true; // Allow user registration without authentication
        }
        
        // For all other endpoints, check for X-User-Id header (simplified authentication)
        String userIdHeader = request.getHeader("X-User-Id");
        
        if (userIdHeader == null || userIdHeader.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Authentication required\",\"status\":401}");
            return false;
        }
        
        try {
            Long.parseLong(userIdHeader);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Invalid user ID format\",\"status\":400}");
            return false;
        }
        
        return true;
    }
}