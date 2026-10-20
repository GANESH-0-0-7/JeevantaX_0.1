
package com.example.demo.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vaultfiles")
public class VaultFile {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String fileName;
    private String ipfsUrl;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public VaultFile() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getIpfsUrl() { return ipfsUrl; }
    public void setIpfsUrl(String ipfsUrl) {
        this.ipfsUrl = ipfsUrl;
    }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

