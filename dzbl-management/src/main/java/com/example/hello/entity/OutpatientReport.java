package com.example.hello.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OutpatientReport {
    private Integer reportId;
    private Integer userId;
    private Integer doctorId;
    private String diagnosis;
    private String medicationAdvice;
    private LocalDateTime createTime;
} 