
package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.GeminiService;
import com.example.demo.utils.SymptomService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {

    private final UserRepository userRepository;
    private final GeminiService geminiService;
    private final SymptomService symptomService;

    public RecommendationService(
            UserRepository userRepository,
            GeminiService geminiService,
            @Qualifier("utilitySymptomService")
            SymptomService symptomService) {

        this.userRepository = userRepository;
        this.geminiService = geminiService;
        this.symptomService = symptomService;
    }

    public User recommendDoctor(String description) {
        String specialization =
                geminiService.getSpecialization(description);

        return userRepository
                .findFirstByRoleAndSpecializationOrderByRatingDesc(
                        "doctor", specialization)
                .orElse(null);
    }

    public Object fetchSymptom(String prompt) {
        return symptomService.analyzeSymptoms(prompt);
    }
}
