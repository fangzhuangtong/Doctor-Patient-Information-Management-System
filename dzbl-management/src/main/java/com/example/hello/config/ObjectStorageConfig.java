package com.example.hello.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectStorageConfig {
    
    private static final String ENDPOINT = "http://object-storage.objectstorage-system.svc.cluster.local";
    private static final String ACCESS_KEY = "tl79jp19";
    private static final String SECRET_KEY = "6vm8dsftp5lk9krw";
    
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(ENDPOINT)
                .credentials(ACCESS_KEY, SECRET_KEY)
                .build();
    }
} 