package com.example.hello.service;

import com.example.hello.entity.MedicalProgress;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface MedicalProgressService {
    List<MedicalProgress> getAllProgresses();
    PageInfo<MedicalProgress> getProgressesByPage(String recordType, Integer doctorId, Integer page, Integer pageSize);
    MedicalProgress getProgressById(Integer reportId);
    void addProgress(MedicalProgress progress);
    void updateProgress(MedicalProgress progress);
    void deleteProgress(Integer reportId);
} 