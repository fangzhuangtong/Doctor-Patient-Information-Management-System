package com.example.hello.service;

import com.example.hello.entity.InpatientRecord;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface InpatientRecordService {
    List<InpatientRecord> getAllRecords();
    PageInfo<InpatientRecord> getRecordsByPage(Integer userId, Integer doctorId, Integer reportId, Integer page, Integer pageSize);
    InpatientRecord getRecordById(Integer recordId);
    void addRecord(InpatientRecord record);
    void updateRecord(InpatientRecord record);
    void deleteRecord(Integer recordId);
} 