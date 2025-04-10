package com.example.hello.service.impl;

import com.example.hello.entity.ExamReport;
import com.example.hello.mapper.ExamReportMapper;
import com.example.hello.service.ExamReportService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamReportServiceImpl implements ExamReportService {
    
    @Autowired
    private ExamReportMapper examReportMapper;
    
    @Override
    public List<ExamReport> getAllReports() {
        return examReportMapper.selectAll();
    }
    
    @Override
    public PageInfo<ExamReport> getReportsByPage(Integer userId, Integer doctorId, String reportType, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        ExamReport condition = new ExamReport();
        condition.setUserId(userId);
        condition.setDoctorId(doctorId);
        condition.setReportType(reportType);
        List<ExamReport> reports = examReportMapper.selectByCondition(condition);
        return new PageInfo<>(reports);
    }
    
    @Override
    public ExamReport getReportById(Integer reportId) {
        return examReportMapper.selectById(reportId);
    }
    
    @Override
    public void addReport(ExamReport report) {
        // 设置默认值
        if (report.getReportType() == null) {
            report.setReportType("检查");
        }
        examReportMapper.insert(report);
    }
    
    @Override
    public void updateReport(ExamReport report) {
        examReportMapper.update(report);
    }
    
    @Override
    public void deleteReport(Integer reportId) {
        examReportMapper.deleteById(reportId);
    }
} 