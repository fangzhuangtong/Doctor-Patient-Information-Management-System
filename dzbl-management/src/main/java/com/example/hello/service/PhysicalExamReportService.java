package com.example.hello.service;

import com.example.hello.entity.PhysicalExamReport;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface PhysicalExamReportService {
    List<PhysicalExamReport> getAllReports();
    PageInfo<PhysicalExamReport> getReportsByPage(Integer userId, Integer doctorId, Integer page, Integer pageSize);
    PhysicalExamReport getReportById(Integer reportId);
    void addReport(PhysicalExamReport report);
    void updateReport(PhysicalExamReport report);
    void deleteReport(Integer reportId);
} 