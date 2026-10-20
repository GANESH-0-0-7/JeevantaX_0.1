
package com.example.demo.controller;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignupRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(
            @RequestBody SignupRequest request,
            HttpServletResponse response) {

        AuthResponse result = authService.signup(request);

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                authService.createTokenCookie(result.getToken()).toString()
        );

        return ResponseEntity.status(201).body(result);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response) {

        AuthResponse result = authService.login(request);

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                authService.createTokenCookie(result.getToken()).toString()
        );

        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = authService.clearTokenCookie();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );

        return ResponseEntity.ok(
                java.util.Map.of(
                        "message",
                        "User Logged Out Successfully"
                )
        );
    }

    @GetMapping("/checkAuth")
    public ResponseEntity<?> checkAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(
                    java.util.Map.of("message", "Unauthorized")
            );
        }

        UserResponse user = authService.getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                java.util.Map.of(
                        "message", "User is authenticated",
                        "user", user
                )
        );
    }
}

