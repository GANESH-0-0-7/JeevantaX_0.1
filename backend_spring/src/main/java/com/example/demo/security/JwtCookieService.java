
package com.example.demo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class JwtCookieService {

    private final boolean production;

    public JwtCookieService(
            @Value("${app.environment:development}")
            String environment) {
        this.production = "production".equalsIgnoreCase(environment);
    }

    public ResponseCookie createTokenCookie(String token) {
        return ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(production)
                .sameSite(production ? "None" : "Lax")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();
    }

    public ResponseCookie clearTokenCookie() {
        return ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(production)
                .sameSite(production ? "None" : "Lax")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
    }
}

