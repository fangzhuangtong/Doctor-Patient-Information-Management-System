package com.example.hello.service;

import com.example.hello.entity.OutpatientReport;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface OutpatientReportService {
    List<OutpatientReport> getAllReports();
    PageInfo<OutpatientReport> getReportsByPage(Integer userId, Integer doctorId, Integer page, Integer pageSize);
    OutpatientReport getReportById(Integer reportId);
    void addReport(OutpatientReport report);
    void updateReport(OutpatientReport report);
    void deleteReport(Integer reportId);
} 