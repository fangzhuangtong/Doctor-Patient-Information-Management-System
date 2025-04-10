package com.example.hello.service;

import com.example.hello.entity.MedicalFile;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface MedicalFileService {
    List<MedicalFile> getAllFiles();
    PageInfo<MedicalFile> getFilesByPage(Integer userId, Integer reportId, String moduleType, Integer page, Integer pageSize);
    MedicalFile getFileById(Integer fileId);
    void addFile(MedicalFile file);
    void updateFile(MedicalFile file);
    void deleteFile(Integer fileId);
} 