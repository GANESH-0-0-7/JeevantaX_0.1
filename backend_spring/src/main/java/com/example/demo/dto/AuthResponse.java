
package com.example.demo.dto;

import com.example.demo.model.User;

public class AuthResponse {

    private final String message;
    private final String token;
    private final UserResponse user;

    public AuthResponse(String message, String token, User user) {
        this.message = message;
        this.token = token;
        this.user = new UserResponse(user);
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public UserResponse getUser() {
        return user;
    }
}
