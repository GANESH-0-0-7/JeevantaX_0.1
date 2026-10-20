
package com.example.demo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

@Service("utilitySymptomService")
public class SymptomService {

    private static final Logger logger =
            LoggerFactory.getLogger(SymptomService.class);

    private static final String MODEL = "gemini-3.6-flash";

    private final Client client;

    public SymptomService(
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

    public String analyzeSymptoms(String description) {

        if (client == null) {
            throw new IllegalStateException(
                    "Gemini API key is not configured");
        }

        String prompt = """
                You are a medical information assistant.

                A patient reports:
                %s

                Provide general health information, not a definitive
                diagnosis. Do not claim certainty about the cause.

                Return this format:

                ðŸ©º Possible Causes
                â€¢ Cause 1
                â€¢ Cause 2

                ðŸ’Š Recommended Actions
                â€¢ Action 1
                â€¢ Action 2

                âš ï¸ Emergency Signs
                â€¢ Sign 1
                â€¢ Sign 2

                Keep the answer below 250 words.
                Mention when urgent medical attention is needed.
                Do not recommend prescription medication or dosages.
                """.formatted(description);

        try {
            GenerateContentResponse response =
                    client.models.generateContent(
                            MODEL, prompt, null);

            String result = response.text();

            if (result == null || result.isBlank()) {
                throw new IllegalStateException(
                        "Gemini returned an empty response");
            }

            return result.trim();

        } catch (Exception exception) {
            logger.error("Gemini symptom analysis failed", exception);
            throw new IllegalStateException(
                    "Unable to analyze symptoms right now",
                    exception);
        }
    }
}

