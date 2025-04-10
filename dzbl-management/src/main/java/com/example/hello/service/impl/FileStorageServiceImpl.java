package com.example.hello.service.impl;

import com.example.hello.service.FileStorageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final String BUCKET_NAME = "tl79jp19-dzbl";
    private static final String ENDPOINT = "https://objectstorageapi.hzh.sealos.run";
    
    @Autowired
    private MinioClient minioClient;
    
    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        try {
            String fileName = UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());
            
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
            
            // 返回完整的访问URL
            return String.format("%s/%s/%s", ENDPOINT, BUCKET_NAME, fileName);
            
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot) : "";
    }
} 