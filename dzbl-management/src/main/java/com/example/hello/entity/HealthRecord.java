package com.example.hello.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class HealthRecord {
    private Integer recordId;
    private Integer userId;
    private String bloodType;
    private BigDecimal height;
    private BigDecimal weight;
    private String bloodPressure;
    private BigDecimal bloodLipid;
    private BigDecimal bloodGlucose;
    private String allergyHistory;
    private String pastMedicalHistory;
    private String familyHistory;
    private String smokingHistory;
    private String alcoholHistory;
    private String dietaryHabits;
    private String exerciseFrequency;
    private String sleepQuality;
    private String psychologicalStatus;
    private BigDecimal bmi;
    private Integer riskScore;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 