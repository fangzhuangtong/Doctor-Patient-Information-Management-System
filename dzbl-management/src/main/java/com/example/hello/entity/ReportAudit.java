package com.example.hello.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReportAudit {
    private Integer auditId;
    private String reportType;
    private Integer reportId;
    private String auditResult;
    private Integer doctorId;
    private LocalDateTime auditTime;
} 