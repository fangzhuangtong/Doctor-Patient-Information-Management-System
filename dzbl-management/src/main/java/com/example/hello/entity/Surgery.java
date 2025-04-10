package com.example.hello.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Surgery {
    private Integer surgeryId;
    private Integer userId;
    private String surgeryName;
    private Integer surgeonId;
    private LocalDateTime surgeryTime;
    private String status;
    private String postoperativeNotes;
} 