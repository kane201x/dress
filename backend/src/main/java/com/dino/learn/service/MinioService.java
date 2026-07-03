package com.dino.learn.service;

import com.dino.learn.config.MinioConfig;
import io.minio.GetObjectArgs;
import io.minio.PutObjectArgs;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class MinioService {

    private static final Logger log = LoggerFactory.getLogger(MinioService.class);

    private final MinioClient minioClient;
    private final String bucketName;

    public MinioService(MinioClient minioClient, MinioConfig minioConfig) {
        this.minioClient = minioClient;
        this.bucketName = minioConfig.getBucketName();
    }

    public void upload(String fileKey, byte[] data, String contentType) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileKey)
                    .stream(new ByteArrayInputStream(data), data.length, -1)
                    .contentType(contentType)
                    .build());
            log.debug("Uploaded to MinIO: {}", fileKey);
        } catch (Exception e) {
            log.error("MinIO upload failed: {}", e.getMessage());
        }
    }

    public byte[] download(String fileKey) {
        try (InputStream is = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(fileKey)
                .build())) {
            return is.readAllBytes();
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("NoSuchKey")) {
                return null;
            }
            log.error("MinIO download error: {}", e.getMessage());
            return null;
        }
    }

    public boolean exists(String fileKey) {
        try {
            minioClient.statObject(
                io.minio.StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileKey)
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
