
package com.example.demo.controller;

import com.example.demo.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<?> createAppointment(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.createAppointment(
                        request, authentication.getName()));
    }

    @GetMapping("/patient")
    public ResponseEntity<?> getPatientAppointments(
            Authentication authentication) {

        return ResponseEntity.ok(
                appointmentService.getPatientAppointments(
                        authentication.getName()));
    }

    @GetMapping("/doctor")
    public ResponseEntity<?> getDoctorAppointments(
            Authentication authentication) {

        return ResponseEntity.ok(
                appointmentService.getDoctorAppointments(
                        authentication.getName()));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAppointmentStatus(
            @PathVariable String id,
            @RequestBody Map<String, Object> request,
            Authentication authentication) {

        return ResponseEntity.ok(
                appointmentService.updateAppointmentStatus(
                        id, request, authentication.getName()));
    }

    @PostMapping("/rate")
    public ResponseEntity<?> rateDoctor(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {

        return ResponseEntity.ok(
                appointmentService.rateDoctor(
                        request, authentication.getName()));
    }
}

