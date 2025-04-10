package com.example.hello.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class User {
    private Integer userId;
    private String name;
    private String gender;
    private LocalDate birthdate;
    private String phone;
    private String idCard;
    private String address;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 