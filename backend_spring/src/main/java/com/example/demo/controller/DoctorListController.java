
package com.example.demo.controller;

import com.example.demo.service.DoctorListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/list")
public class DoctorListController {

    private final DoctorListService doctorListService;

    public DoctorListController(DoctorListService doctorListService) {
        this.doctorListService = doctorListService;
    }

    @GetMapping("/doctors")
    public ResponseEntity<?> getDoctors() {
        return ResponseEntity.ok(
                Map.of(
                        "message", "Doctors fetched successfully",
                        "doctors", doctorListService.getDoctors()
                )
        );
    }
}

