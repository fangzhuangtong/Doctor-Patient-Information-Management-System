package com.example.hello.entity;

import lombok.Data;
import java.time.LocalDateTime;
import com.alibaba.fastjson.JSONObject;

@Data
public class PhysicalExamReport {
    private Integer reportId;
    private Integer userId;
    private Integer examId;
    private Integer doctorId;
    private JSONObject results;
    private String suggestions;
    private LocalDateTime createTime;
} 