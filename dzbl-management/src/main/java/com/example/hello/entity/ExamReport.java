package com.example.hello.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExamReport {
    private Integer reportId;
    private Integer userId;
    private Integer doctorId;
    private String reportType;
    private String conclusion;
    private String advice;
    private LocalDateTime createTime;
} 