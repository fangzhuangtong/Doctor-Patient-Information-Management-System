package com.example.hello.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MedicalProgress {
    private Integer reportId;
    private String recordType;
    private Integer recordId;
    private String content;
    private Integer doctorId;
    private LocalDateTime createTime;
    private Integer userId;
} 