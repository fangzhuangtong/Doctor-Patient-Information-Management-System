package com.example.hello.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExamBooking {
    private Integer examId;
    private Integer userId;
    private String examType;
    private LocalDateTime scheduleTime;
    private String location;
    private String status;
    private BigDecimal fee;
    private LocalDateTime createTime;
    private Integer doctorId;
} 