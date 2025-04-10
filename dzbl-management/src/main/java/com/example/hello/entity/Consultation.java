package com.example.hello.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Consultation {
    private Integer consultId;
    private Integer userId;
    private Integer doctorId;
    private String consultType;
    private String content;
    private LocalDateTime consultTime;
    private String status;
    private String replyContent;
} 