package com.example.hello.entity;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DoctorSchedule {
    private Integer scheduleId;
    private Integer doctorId;
    private String scheduleType;
    private LocalDate scheduleDate;
    private String timeRange;
    private Integer maxPatient;
    private String doctorName;
    private String department;
} 