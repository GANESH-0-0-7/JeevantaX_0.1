
package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.DoctorRecommendationRequest;
import com.example.demo.dto.SymptomRequest;
import com.example.demo.model.User;
import com.example.demo.service.RecommendationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(
            RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/recommend-doctor")
    public ResponseEntity<ApiResponse<?>> recommendDoctor(
            @RequestBody DoctorRecommendationRequest request) {

        if (request.getDescription() == null
                || request.getDescription().isBlank()) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(
                    "Please provide description", null));
        }

        try {
            User doctor = recommendationService
                    .recommendDoctor(request.getDescription());

            if (doctor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                        "No doctor available for this specialization",
                        null));
            }

            return ResponseEntity.ok(
                new ApiResponse<>(
                    "Doctor recommended successfully", doctor));

        } catch (Exception error) {
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(
                    "Internal Server Error", null));
        }
    }

    @PostMapping("/fetch-symptom")
    public ResponseEntity<ApiResponse<?>> fetchSymptom(
            @RequestBody SymptomRequest request) {

        if (request.getPrompt() == null
                || request.getPrompt().isBlank()) {
            return ResponseEntity.badRequest().body(
                new ApiResponse<>(
                    "Please provide prompt", null));
        }

        try {
            Object response = recommendationService
                    .fetchSymptom(request.getPrompt());

            return ResponseEntity.ok(
                new ApiResponse<>(
                    "Symptoms Fetched Successfully", response));

        } catch (Exception error) {
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(
                    "Internal Server Error", null));
        }
    }
}

