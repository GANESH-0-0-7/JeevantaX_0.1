
package com.example.demo.utils;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeminiService {

    private static final Logger logger =
            LoggerFactory.getLogger(GeminiService.class);

    private static final String MODEL = "gemini-3.6-flash";

    private static final String DEFAULT_SPECIALIZATION =
            "General Medicine";

    private static final List<String> SPECIALIZATIONS = List.of(
            "Cardiology",
            "Dermatology",
            "Neurology",
            "Orthopedics",
            "Psychiatry",
            "Pediatrics",
            "Radiology",
            "Oncology",
            "General Medicine"
    );

    private final Client client;

    public GeminiService(
            @Value("${gemini.api-key:}") String apiKey) {

        if (apiKey == null || apiKey.isBlank()) {
            this.client = null;
            logger.warn("Gemini API key is not configured");
        } else {
            this.client = Client.builder()
                    .apiKey(apiKey)
                    .build();
        }
    }

    public String getSpecialization(String description) {

        if (client == null) {
            return DEFAULT_SPECIALIZATION;
        }

        String prompt = """
                Return ONLY one specialization.

                Symptoms:
                %s

                Choose exactly one from this list:
                Cardiology
                Dermatology
                Neurology
                Orthopedics
                Psychiatry
                Pediatrics
                Radiology
                Oncology
                General Medicine
                """.formatted(description);

        try {
            GenerateContentResponse response =
                    client.models.generateContent(
                            MODEL, prompt, null);

            String result = response.text();

            if (result == null || result.isBlank()) {
                return DEFAULT_SPECIALIZATION;
            }

            String specialization = result.trim();

            return SPECIALIZATIONS.stream()
                    .filter(s -> s.equalsIgnoreCase(specialization))
                    .findFirst()
                    .orElse(DEFAULT_SPECIALIZATION);

        } catch (Exception exception) {
            logger.error(
                    "Gemini specialization request failed",
                    exception);

            return DEFAULT_SPECIALIZATION;
        }
    }
}

