package com.example.hello.service;

import com.example.hello.entity.HealthRecord;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface HealthRecordService {
    List<HealthRecord> getAllRecords();
    PageInfo<HealthRecord> getRecordsByPage(Integer userId, String bloodType, Integer page, Integer pageSize);
    HealthRecord getRecordById(Integer recordId);
    void addRecord(HealthRecord record);
    void updateRecord(HealthRecord record);
    void deleteRecord(Integer recordId);
    List<HealthRecord> getRecordsByUserId(Integer userId);
} 