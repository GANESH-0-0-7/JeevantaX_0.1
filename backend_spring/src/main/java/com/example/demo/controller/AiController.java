
package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.DoctorRecommendationRequest;
import com.example.demo.dto.SymptomRequest;
import com.example.demo.model.User;
import com.example.demo.service.RecommendationService;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final RecommendationService recommendationService;

    public AiController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/recommendDoctor")
    public ResponseEntity<?> recommendDoctor(
            @RequestBody DoctorRecommendationRequest request) {

        if (request.getDescription() == null
                || request.getDescription().isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Please provide description")
            );
        }

        User doctor = recommendationService.recommendDoctor(
                request.getDescription()
        );

        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "message",
                            "No doctor available for this specialization"
                    )
            );
        }

        return ResponseEntity.ok(doctor);
    }

    @PostMapping("/symptom")
    public ResponseEntity<?> fetchSymptom(
            @RequestBody SymptomRequest request) {

        if (request.getPrompt() == null
                || request.getPrompt().isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Please provide prompt")
            );
        }

        Object result = recommendationService.fetchSymptom(
                request.getPrompt()
        );

        return ResponseEntity.ok(result);
    }
}
