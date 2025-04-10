package com.example.hello.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Registration {
    private Integer regId;
    private Integer userId;
    private Integer departmentId;
    private Integer doctorId;
    private LocalDateTime regTime;
    private String status;
    private BigDecimal fee;
    private LocalDateTime createTime;
} 