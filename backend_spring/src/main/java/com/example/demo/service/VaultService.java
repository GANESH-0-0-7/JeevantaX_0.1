
package com.example.demo.service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.VaultFile;
import com.example.demo.repository.VaultFileRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class VaultService {

    private final VaultFileRepository repository;
    private final RestClient pinataClient;
    private final ObjectMapper objectMapper;

    public VaultService(
            VaultFileRepository repository,
            ObjectMapper objectMapper,
            @Value("${pinata.jwt}") String jwt,
            @Value("${pinata.api-url}") String apiUrl) {

        this.repository = repository;
        this.objectMapper = objectMapper;

        String authorization = jwt.startsWith("Bearer ")
                ? jwt
                : "Bearer " + jwt;

        this.pinataClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        authorization)
                .build();
    }

    public VaultFile uploadToPinata(
            MultipartFile file,
            String userId) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "No file uploaded");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {
            fileName = "uploaded-file";
        }

        // Use a final variable inside the anonymous inner class.
        final String uploadFileName = fileName;

        try {
            byte[] bytes = file.getBytes();

            ByteArrayResource resource =
                    new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return uploadFileName;
                }
            };

            MultiValueMap<String, Object> parts =
                    new LinkedMultiValueMap<>();

            parts.add("file", resource);

            parts.add(
                    "pinataMetadata",
                    objectMapper.writeValueAsString(
                            Map.of("name", fileName)));

            JsonNode result = pinataClient.post()
                    .uri("/pinning/pinFileToIPFS")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(parts)
                    .retrieve()
                    .body(JsonNode.class);

            if (result == null
                    || !result.hasNonNull("IpfsHash")) {
                throw new IllegalStateException(
                        "Pinata returned no IPFS hash");
            }

            String ipfsHash =
                    result.get("IpfsHash").asText();

            String ipfsUrl =
                    "https://gateway.pinata.cloud/ipfs/"
                            + ipfsHash;

            Instant now = Instant.now();

            VaultFile vaultFile = new VaultFile();
            vaultFile.setUserId(userId);
            vaultFile.setFileName(fileName);
            vaultFile.setIpfsUrl(ipfsUrl);
            vaultFile.setCreatedAt(now);
            vaultFile.setUpdatedAt(now);

            return repository.save(vaultFile);

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to read uploaded file",
                    exception);
        }
    }

    public List<VaultFile> getMyVaultFiles(String userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(
                userId);
    }
}
