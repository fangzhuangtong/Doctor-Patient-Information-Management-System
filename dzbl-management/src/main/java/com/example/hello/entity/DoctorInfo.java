package com.example.hello.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DoctorInfo {
    private Integer doctorId;
    private Integer departmentId;
    private String title;
    private String licenseNo;
    private LocalDateTime createTime;
    private String introduction;
    private String doctorName;
} 