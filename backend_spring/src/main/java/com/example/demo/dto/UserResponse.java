
package com.example.demo.dto;

import java.util.List;

import com.example.demo.model.User;

public class UserResponse {

    private final String id;
    private final String name;
    private final String email;
    private final String role;
    private final String specialization;
    private final List<String> workingHours;
    private final Double rating;
    private final String chatId;

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.specialization = user.getSpecialization();
        this.workingHours = user.getWorkingHours();
        this.rating = user.getRating();
        this.chatId = user.getChatId();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getSpecialization() {
        return specialization;
    }

    public List<String> getWorkingHours() {
        return workingHours;
    }

    public Double getRating() {
        return rating;
    }

    public String getChatId() {
        return chatId;
    }
}
