
package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class SymptomService {

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public SymptomService(
            @Value("${gemini.api.key}") String apiKey,
            @Value("${gemini.api.model:gemini-2.5-flash}") String model) {

        this.apiKey = apiKey;
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }

    public String fetchSymptoms(String desc) {
        if (desc == null || desc.isBlank()) {
            throw new IllegalArgumentException(
                    "Symptom description is required");
        }

        String prompt = """
                You are an AI assistant providing general health information,
                not a medical diagnosis.

                A patient reports:
                %s

                Return ONLY this format:

                🩺 Possible Causes
                • Cause 1
                • Cause 2

                💊 Recommended Actions
                • Action 1
                • Action 2

                ⚠️ Emergency Signs
                • Sign 1
                • Sign 2

                Keep the answer below 250 words.
                Encourage urgent medical care for severe or life-threatening
                symptoms. Do not claim certainty about a diagnosis.
                """.formatted(desc);

        try {
            JsonNode response = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/{model}:generateContent")
                            .queryParam("key", apiKey)
                            .build(model))
                    .body(Map.of(
                            "contents", List.of(
                                    Map.of(
                                            "parts", List.of(
                                                    Map.of("text", prompt)
                                            )
                                    )
                            )
                    ))
                    .retrieve()
                    .body(JsonNode.class);

            JsonNode parts = response == null
                    ? null
                    : response.path("candidates")
                              .path(0)
                              .path("content")
                              .path("parts");

            if (parts == null || !parts.isArray() || parts.isEmpty()
                    || !parts.get(0).hasNonNull("text")) {
                throw new IllegalStateException(
                        "Gemini returned no symptom text");
            }

            return parts.get(0).path("text").asText();

        } catch (Exception error) {
            // Avoid logging API keys, request URLs, or patient descriptions.
            System.err.println(
                    "Gemini symptom request failed: "
                            + error.getClass().getSimpleName());

            throw new RuntimeException(
                    "Failed to fetch symptom information", error);
        }
    }
}
