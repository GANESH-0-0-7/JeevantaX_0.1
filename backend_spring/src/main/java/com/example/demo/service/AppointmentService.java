package com.example.demo.service;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.model.Appointment;
import com.example.demo.model.User;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.UserRepository;
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    public Appointment createAppointment(
            Map<String, Object> request, String patientId) {

        User patient = getUser(patientId);

        if (!"patient".equalsIgnoreCase(patient.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only patients can create appointments");
        }

        String doctorId = requiredString(request, "doctorId");
        String time = requiredString(request, "time");

        User doctor = getUser(doctorId);

        if (!"doctor".equalsIgnoreCase(doctor.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selected user is not a doctor");
        }

        Appointment appointment = new Appointment();
        appointment.setPatientId(patient.getId());
        appointment.setDoctorId(doctor.getId());
        appointment.setTime(time);

        Object documentUrl = request.get("documentUrl");
        if (documentUrl instanceof String url) {
            appointment.setDocumentUrl(url);
        }

        appointment.setStatus("pending");
        appointment.setCreatedAt(Instant.now());
        appointment.setUpdatedAt(Instant.now());

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getPatientAppointments(String patientId) {
        User user = getUser(patientId);

        if (!"patient".equalsIgnoreCase(user.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Patient account required");
        }

        return appointmentRepository
                .findByPatientIdOrderByCreatedAtDesc(user.getId());
    }

    public List<Appointment> getDoctorAppointments(String doctorId) {
        User user = getUser(doctorId);

        if (!"doctor".equalsIgnoreCase(user.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Doctor account required");
        }

        return appointmentRepository
                .findByDoctorIdOrderByCreatedAtDesc(user.getId());
    }

    public Appointment updateAppointmentStatus(
            String appointmentId,
            Map<String, Object> request,
            String doctorId) {

        User doctor = getUser(doctorId);

        if (!"doctor".equalsIgnoreCase(doctor.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only doctors can update appointment status");
        }

        String status = requiredString(request, "status")
                .toLowerCase(Locale.ROOT);

        if (!Set.of("accepted", "cancelled", "completed")
                .contains(status)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status must be accepted, cancelled or completed");
        }

        Appointment appointment = appointmentRepository
                .findByIdAndDoctorId(appointmentId, doctor.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Appointment not found"));

        if (!"pending".equals(appointment.getStatus())
                && !("accepted".equals(appointment.getStatus())
                && ("completed".equals(status)
                || "cancelled".equals(status)))) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This appointment cannot transition to that status");
        }

        appointment.setStatus(status);
        appointment.setUpdatedAt(Instant.now());

        return appointmentRepository.save(appointment);
    }

    public Map<String, Object> rateDoctor(
            Map<String, Object> request,
            String patientId) {

        User patient = getUser(patientId);

        if (!"patient".equalsIgnoreCase(patient.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only patients can rate doctors");
        }

        String appointmentId = requiredString(request, "appointmentId");

        Object ratingValue = request.get("rating");
        if (!(ratingValue instanceof Number number)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A numeric rating is required");
        }

        double rating = number.doubleValue();

        if (rating < 1 || rating > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rating must be between 1 and 5");
        }

        Appointment appointment = appointmentRepository
                .findByIdAndPatientIdAndStatus(
                        appointmentId, patient.getId(), "completed")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Completed appointment not found"));

        if (appointment.getRating() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This appointment has already been rated");
        }

        appointment.setRating(rating);
        appointment.setUpdatedAt(Instant.now());
        appointmentRepository.save(appointment);

        User doctor = getUser(appointment.getDoctorId());

        List<Appointment> appointments =
                appointmentRepository
                        .findByDoctorIdOrderByCreatedAtDesc(doctor.getId());

        double total = 0;
        int count = 0;

        for (Appointment item : appointments) {
            if (item.getRating() != null) {
                total += item.getRating();
                count++;
            }
        }

        doctor.setRating(count == 0 ? 0.0 : total / count);
        userRepository.save(doctor);

        return Map.of(
                "message", "Doctor rated successfully",
                "rating", rating,
                "doctorRating", doctor.getRating());
    }

    private User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"));
    }

    private String requiredString(
            Map<String, Object> request, String field) {

        Object value = request.get(field);

        if (!(value instanceof String string) || string.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    field + " is required");
        }

        return string.trim();
    }
}
 
