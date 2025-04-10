package com.example.hello.service;

import com.example.hello.entity.OutpatientRecord;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface OutpatientRecordService {
    List<OutpatientRecord> getAllRecords();
    PageInfo<OutpatientRecord> getRecordsByPage(Integer userId, Integer doctorId, Integer reportId, Integer page, Integer pageSize);
    OutpatientRecord getRecordById(Integer recordId);
    void addRecord(OutpatientRecord record);
    void updateRecord(OutpatientRecord record);
    void deleteRecord(Integer recordId);
} 