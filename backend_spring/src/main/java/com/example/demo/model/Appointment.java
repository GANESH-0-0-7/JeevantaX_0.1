
package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "appointments")
public class Appointment {

    @Id
    private String id;

    @Indexed
    private String patientId;

    @Indexed
    private String doctorId;

    private String time;

    private String status = "pending";

    private String documentUrl;

    private Double rating;

    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();

    public Appointment() {}

    public void validateStatus() {
        if (status == null ||
            !java.util.Set.of(
                "pending", "accepted", "cancelled", "completed"
            ).contains(status)) {
            throw new IllegalArgumentException(
                "Invalid appointment status"
            );
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

