package com.example.hello.service;

import com.example.hello.entity.InpatientReport;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface InpatientReportService {
    List<InpatientReport> getAllReports();
    PageInfo<InpatientReport> getReportsByPage(Integer userId, Integer doctorId, String reportType, Integer page, Integer pageSize);
    InpatientReport getReportById(Integer reportId);
    void addReport(InpatientReport report);
    void updateReport(InpatientReport report);
    void deleteReport(Integer reportId);
} 