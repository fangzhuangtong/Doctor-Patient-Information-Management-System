package com.example.hello.service.impl;

import com.example.hello.entity.MedicalFile;
import com.example.hello.mapper.MedicalFileMapper;
import com.example.hello.service.MedicalFileService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalFileServiceImpl implements MedicalFileService {
    
    @Autowired
    private MedicalFileMapper medicalFileMapper;
    
    @Override
    public List<MedicalFile> getAllFiles() {
        return medicalFileMapper.selectAll();
    }
    
    @Override
    public PageInfo<MedicalFile> getFilesByPage(Integer userId, Integer reportId, String moduleType, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        MedicalFile condition = new MedicalFile();
        condition.setUserId(userId);
        condition.setReportId(reportId);
        condition.setModuleType(moduleType);
        List<MedicalFile> files = medicalFileMapper.selectByCondition(condition);
        return new PageInfo<>(files);
    }
    
    @Override
    public MedicalFile getFileById(Integer fileId) {
        return medicalFileMapper.selectById(fileId);
    }
    
    @Override
    public void addFile(MedicalFile file) {
        medicalFileMapper.insert(file);
    }
    
    @Override
    public void updateFile(MedicalFile file) {
        medicalFileMapper.update(file);
    }
    
    @Override
    public void deleteFile(Integer fileId) {
        medicalFileMapper.deleteById(fileId);
    }
} 