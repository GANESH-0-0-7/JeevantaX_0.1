
package com.example.demo.dto;

public class RateDoctorRequest {
    private String appointmentId;
    private Double rating;

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
}

