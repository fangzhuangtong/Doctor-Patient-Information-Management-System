package com.example.hello.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Hospitalization {
    private Integer hosId;
    private Integer userId;
    private LocalDateTime admitTime;
    private LocalDateTime dischargeTime;
    private String bedNumber;
    private Integer doctorId;
    private String summary;
    private String status;
} 