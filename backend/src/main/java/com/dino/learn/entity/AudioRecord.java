package com.dino.learn.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audio_records")
public class AudioRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_key", nullable = false, unique = true, length = 255)
    private String fileKey;

    @Column(name = "text_content", nullable = false, length = 500)
    private String textContent;

    @Column(name = "language", nullable = false, length = 20)
    private String language;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public AudioRecord() {}

    public AudioRecord(String fileKey, String textContent, String language, Long fileSize) {
        this.fileKey = fileKey;
        this.textContent = textContent;
        this.language = language;
        this.fileSize = fileSize;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFileKey() { return fileKey; }
    public void setFileKey(String fileKey) { this.fileKey = fileKey; }
    public String getTextContent() { return textContent; }
    public void setTextContent(String textContent) { this.textContent = textContent; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
