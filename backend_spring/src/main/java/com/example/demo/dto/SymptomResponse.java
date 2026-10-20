
package com.example.demo.dto;

public class SymptomResponse {

    private final String message;
    private final String response;

    public SymptomResponse(String message, String response) {
        this.message = message;
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public String getResponse() {
        return response;
    }
}
