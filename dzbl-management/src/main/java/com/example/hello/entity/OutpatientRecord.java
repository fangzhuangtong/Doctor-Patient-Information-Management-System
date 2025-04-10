package com.example.hello.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OutpatientRecord {
    private Integer recordId;
    private Integer registerId;
    private Integer userId;
    private String chiefComplaint;
    private String diagnosis;
    private String prescription;
    private Integer doctorId;
    private LocalDateTime createTime;
    private Integer reportId;
} 