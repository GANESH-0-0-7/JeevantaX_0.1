
package com.example.demo.dto;

public class CreateAppointmentRequest {
    private String doctorId;
    private String time;
    private String documentUrl;

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }
}

