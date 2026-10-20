
package com.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class HospitalService {

    private final RestClient restClient;

    public HospitalService(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://overpass-api.de")
                .defaultHeader("User-Agent", "JeevantaX/1.0")
                .build();
    }

    public List<Map<String, Object>> getNearbyHospitals(
            double lat, double lon) {

        String query = """
                [out:json];
                (
                  node["amenity"="hospital"](around:5000,%f,%f);
                  way["amenity"="hospital"](around:5000,%f,%f);
                  relation["amenity"="hospital"](around:5000,%f,%f);
                );
                out center tags;
                """.formatted(lat, lon, lat, lon, lat, lon);

        JsonNode data = restClient.post()
                .uri("/api/interpreter")
                .contentType(MediaType.TEXT_PLAIN)
                .body(query)
                .retrieve()
                .body(JsonNode.class);

        List<Map<String, Object>> hospitals = new ArrayList<>();

        if (data == null || !data.has("elements")) {
            return hospitals;
        }

        for (JsonNode item : data.path("elements")) {
            JsonNode tags = item.path("tags");

            Map<String, Object> hospital = new LinkedHashMap<>();
            hospital.put("id", item.path("id").asLong());
            hospital.put("name", tagOrDefault(
                    tags, "name", "Unnamed Hospital"));
            hospital.put("address", firstAvailableTag(
                    tags, "addr:full", "addr:street",
                    "Address unavailable"));
            hospital.put("phone", tagOrDefault(
                    tags, "phone", "N/A"));

            JsonNode center = item.path("center");

            if (item.has("lat")) {
                hospital.put("lat", item.path("lat").asDouble());
            } else if (center.has("lat")) {
                hospital.put("lat", center.path("lat").asDouble());
            } else {
                hospital.put("lat", null);
            }

            if (item.has("lon")) {
                hospital.put("lng", item.path("lon").asDouble());
            } else if (center.has("lon")) {
                hospital.put("lng", center.path("lon").asDouble());
            } else {
                hospital.put("lng", null);
            }

            hospitals.add(hospital);
        }

        return hospitals;
    }

    private String tagOrDefault(
            JsonNode tags, String key, String fallback) {
        String value = tags.path(key).asText("");
        return value.isBlank() ? fallback : value;
    }

    private String firstAvailableTag(
            JsonNode tags, String first, String second,
            String fallback) {
        String value = tags.path(first).asText("");
        if (!value.isBlank()) return value;

        value = tags.path(second).asText("");
        return value.isBlank() ? fallback : value;
    }
}

