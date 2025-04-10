package com.example.hello.service.impl;

import com.example.hello.entity.MedicalProgress;
import com.example.hello.mapper.MedicalProgressMapper;
import com.example.hello.service.MedicalProgressService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalProgressServiceImpl implements MedicalProgressService {
    
    @Autowired
    private MedicalProgressMapper medicalProgressMapper;
    
    @Override
    public List<MedicalProgress> getAllProgresses() {
        return medicalProgressMapper.selectAll();
    }
    
    @Override
    public PageInfo<MedicalProgress> getProgressesByPage(String recordType, Integer doctorId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        MedicalProgress condition = new MedicalProgress();
        condition.setRecordType(recordType);
        condition.setDoctorId(doctorId);
        List<MedicalProgress> progresses = medicalProgressMapper.selectByCondition(condition);
        return new PageInfo<>(progresses);
    }
    
    @Override
    public MedicalProgress getProgressById(Integer reportId) {
        return medicalProgressMapper.selectById(reportId);
    }
    
    @Override
    public void addProgress(MedicalProgress progress) {
        medicalProgressMapper.insert(progress);
    }
    
    @Override
    public void updateProgress(MedicalProgress progress) {
        medicalProgressMapper.update(progress);
    }
    
    @Override
    public void deleteProgress(Integer reportId) {
        medicalProgressMapper.deleteById(reportId);
    }
} 