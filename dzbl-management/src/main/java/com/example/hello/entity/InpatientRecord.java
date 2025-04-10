package com.example.hello.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InpatientRecord {
    private Integer recordId;
    private Integer hospitalId;
    private Integer userId;
    private String admissionDiagnosis;
    private String dischargeSummary;
    private Integer doctorId;
    private LocalDateTime createTime;
    private Integer reportId;
} 