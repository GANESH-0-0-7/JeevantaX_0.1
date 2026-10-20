
package com.example.demo.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.DoctorRecommendationResponse;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.GeminiService;

@Service
public class AiService {

    private final UserRepository userRepository;
    private final GeminiService geminiService;
    private final SymptomService symptomService;

    public AiService(
            UserRepository userRepository,
            GeminiService geminiService,
            SymptomService symptomService) {
        this.userRepository = userRepository;
        this.geminiService = geminiService;
        this.symptomService = symptomService;
    }

    public DoctorRecommendationResponse recommendDoctor(
            String description) {

        String specialization =
                geminiService.getSpecialization(description);

        User doctor = userRepository
                .findFirstByRoleAndSpecializationOrderByRatingDesc(
                        "doctor",
                        specialization)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No doctor available for this specialization"));

        return new DoctorRecommendationResponse(
                "Doctor recommended successfully",
                doctor);
    }

    public String fetchSymptoms(String prompt) {
        return symptomService.fetchSymptoms(prompt);
    }
}
