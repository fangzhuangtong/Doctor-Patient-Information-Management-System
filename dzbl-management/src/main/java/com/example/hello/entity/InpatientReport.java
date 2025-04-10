package com.example.hello.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InpatientReport {
    private Integer reportId;
    private Integer userId;
    private String reportType;
    private Integer doctorId;
    private String diagnosis;
    private String medicationAdvice;
    private LocalDateTime createTime;
} 