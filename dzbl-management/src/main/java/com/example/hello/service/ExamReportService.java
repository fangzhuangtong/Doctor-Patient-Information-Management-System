package com.example.hello.service;

import com.example.hello.entity.ExamReport;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface ExamReportService {
    List<ExamReport> getAllReports();
    PageInfo<ExamReport> getReportsByPage(Integer userId, Integer doctorId, String reportType, Integer page, Integer pageSize);
    ExamReport getReportById(Integer reportId);
    void addReport(ExamReport report);
    void updateReport(ExamReport report);
    void deleteReport(Integer reportId);
} 