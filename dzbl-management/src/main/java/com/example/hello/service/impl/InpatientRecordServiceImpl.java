package com.example.hello.service.impl;

import com.example.hello.entity.InpatientRecord;
import com.example.hello.mapper.InpatientRecordMapper;
import com.example.hello.service.InpatientRecordService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InpatientRecordServiceImpl implements InpatientRecordService {
    
    @Autowired
    private InpatientRecordMapper inpatientRecordMapper;
    
    @Override
    public List<InpatientRecord> getAllRecords() {
        return inpatientRecordMapper.selectAll();
    }
    
    @Override
    public PageInfo<InpatientRecord> getRecordsByPage(Integer userId, Integer doctorId, Integer reportId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        InpatientRecord condition = new InpatientRecord();
        condition.setUserId(userId);
        condition.setDoctorId(doctorId);
        condition.setReportId(reportId);
        List<InpatientRecord> records = inpatientRecordMapper.selectByCondition(condition);
        return new PageInfo<>(records);
    }
    
    @Override
    public InpatientRecord getRecordById(Integer recordId) {
        return inpatientRecordMapper.selectById(recordId);
    }
    
    @Override
    public void addRecord(InpatientRecord record) {
        inpatientRecordMapper.insert(record);
    }
    
    @Override
    public void updateRecord(InpatientRecord record) {
        inpatientRecordMapper.update(record);
    }
    
    @Override
    public void deleteRecord(Integer recordId) {
        inpatientRecordMapper.deleteById(recordId);
    }
} 