
package com.example.demo.dto;

import com.example.demo.model.User;

public class DoctorRecommendationResponse {

    private final String message;
    private final User doctor;

    public DoctorRecommendationResponse(String message, User doctor) {
        this.message = message;
        this.doctor = doctor;
    }

    public String getMessage() {
        return message;
    }

    public User getDoctor() {
        return doctor;
    }
}
