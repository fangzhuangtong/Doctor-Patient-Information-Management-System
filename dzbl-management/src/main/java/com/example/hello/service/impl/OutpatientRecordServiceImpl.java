package com.example.hello.service.impl;

import com.example.hello.entity.OutpatientRecord;
import com.example.hello.mapper.OutpatientRecordMapper;
import com.example.hello.service.OutpatientRecordService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutpatientRecordServiceImpl implements OutpatientRecordService {
    
    @Autowired
    private OutpatientRecordMapper outpatientRecordMapper;
    
    @Override
    public List<OutpatientRecord> getAllRecords() {
        return outpatientRecordMapper.selectAll();
    }
    
    @Override
    public PageInfo<OutpatientRecord> getRecordsByPage(Integer userId, Integer doctorId, Integer reportId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        OutpatientRecord condition = new OutpatientRecord();
        condition.setUserId(userId);
        condition.setDoctorId(doctorId);
        condition.setReportId(reportId);
        List<OutpatientRecord> records = outpatientRecordMapper.selectByCondition(condition);
        return new PageInfo<>(records);
    }
    
    @Override
    public OutpatientRecord getRecordById(Integer recordId) {
        return outpatientRecordMapper.selectById(recordId);
    }
    
    @Override
    public void addRecord(OutpatientRecord record) {
        outpatientRecordMapper.insert(record);
    }
    
    @Override
    public void updateRecord(OutpatientRecord record) {
        outpatientRecordMapper.update(record);
    }
    
    @Override
    public void deleteRecord(Integer recordId) {
        outpatientRecordMapper.deleteById(recordId);
    }
} 