package com.example.hello.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MedicalFile {
    private Integer fileId;
    private Integer userId;
    private Integer reportId;
    private String moduleType;
    private String fileUrl;
    private LocalDateTime uploadTime;
} 