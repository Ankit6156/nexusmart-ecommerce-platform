package com.kodnest.app.filter;

import com.kodnest.app.entities.Role;
import com.kodnest.app.entities.User;
import com.kodnest.app.repositories.UserRepository;
import com.kodnest.app.userServices.AuthServiceContract;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@WebFilter(urlPatterns = {"/api/*", "/admin/*"})
@Component
public class AuthenticationFilter implements Filter {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthenticationFilter.class);

    private final AuthServiceContract authService;
    private final UserRepository userRepository;

    private static final String ALLOWED_ORIGIN = "http://localhost:5173";

    // 🔓 Public endpoints (NO AUTH REQUIRED)
    private static final String[] UNAUTHENTICATED_PATHS = {
            "/api/users/register",
            "/api/auth/login",
            "/api/auth/verify",
            "/api/auth/forgot-password",
            "/api/auth/forgot-password/verify-otp",
            "/api/auth/forgot-password/reset"
    };

    public AuthenticationFilter(AuthServiceContract authService,
                                UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        logger.info("AuthenticationFilter initialized");
        System.out.println("Filter is StartedWorking");
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        logger.info("Incoming request: {}", requestURI);

        // ✈️ CORS Preflight
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            setCORSHeaders(httpResponse);
            return;
        }

        // allow  Unauthenticated path
        if (Arrays.asList(UNAUTHENTICATED_PATHS).contains(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        // 🔐 Extract JWT from HttpOnly cookie
        String token = getAuthTokenFromCookies(httpRequest);

        if (token == null || !authService.validateToken(token)) {
            setCORSHeaders(httpResponse);
            sendErrorResponse(
                    httpResponse,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized: Invalid or missing token"
            );
            return;
        }

        // 🔍 Extract username from JWT
        String username = authService.extractUsername(token);

        Optional<User> userOptional =
                userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            setCORSHeaders(httpResponse);
            sendErrorResponse(
                    httpResponse,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized: User not found"
            );
            return;
        }

        User authenticatedUser = userOptional.get();
        Role role = authenticatedUser.getRole();

        logger.info("Authenticated user: {}, role: {}",
                authenticatedUser.getUsername(), role);

        // 🔒 Role-based authorization
        if (requestURI.startsWith("/admin/")
                && role != Role.ADMIN) {

            setCORSHeaders(httpResponse);
            sendErrorResponse(
                    httpResponse,
                    HttpServletResponse.SC_FORBIDDEN,
                    "Forbidden: Admin access required"
            );
            return;
        }

        if (requestURI.startsWith("/api/")
                && role != Role.CUSTOMER) {

            setCORSHeaders(httpResponse);
            sendErrorResponse(
                    httpResponse,
                    HttpServletResponse.SC_FORBIDDEN,
                    "Forbidden: Customer access required"
            );
            return;
        }

        // 🎫 Attach authenticated user to request
        httpRequest.setAttribute("authenticatedUser", authenticatedUser);

        chain.doFilter(request, response);
    }

    // 🍪 Extract JWT from cookie
    private String getAuthTokenFromCookies(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> "authToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    // 🌐 CORS Headers
    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
        response.setHeader("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers",
                "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    // ❌ Error Response (JSON)
    private void sendErrorResponse(HttpServletResponse response,
                                   int statusCode,
                                   String message) throws IOException {

        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.getWriter().write(
                "{ \"error\": \"" + message + "\" }"
        );
    }
}
