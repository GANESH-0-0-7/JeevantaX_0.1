
package com.example.demo.controller;

import com.example.demo.service.HospitalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/therapists")
public class HospitalController {

    private final HospitalService hospitalService;

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyHospitals(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon) {

        if (lat == null || lon == null) {
            return ResponseEntity.badRequest().body(
                    java.util.Map.of(
                            "message",
                            "Latitude and Longitude are required"
                    )
            );
        }

        if (!Double.isFinite(lat) || !Double.isFinite(lon)
                || lat < -90 || lat > 90
                || lon < -180 || lon > 180) {
            return ResponseEntity.badRequest().body(
                    java.util.Map.of("message", "Invalid coordinates")
            );
        }

        return ResponseEntity.ok(
                hospitalService.getNearbyHospitals(lat, lon)
        );
    }
}

