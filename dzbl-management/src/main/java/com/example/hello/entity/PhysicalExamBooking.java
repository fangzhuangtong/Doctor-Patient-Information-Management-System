package com.example.hello.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PhysicalExamBooking {
    private Integer examId;
    private Integer userId;
    private String packageType;
    private LocalDateTime examTime;
    private String institution;
    private String status;
    private LocalDateTime createTime;
    private Integer doctorId;
} 