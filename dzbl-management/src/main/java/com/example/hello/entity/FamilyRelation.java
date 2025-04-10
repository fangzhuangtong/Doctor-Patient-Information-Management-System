package com.example.hello.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FamilyRelation {
    private Integer relationId;
    private Integer patientId;
    private Integer relativeId;
    private String relationType;
    private LocalDateTime bindTime;
    private Boolean isValid;
} 