package com.example.hello.service.impl;

import com.example.hello.entity.PhysicalExamReport;
import com.example.hello.mapper.PhysicalExamReportMapper;
import com.example.hello.service.PhysicalExamReportService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhysicalExamReportServiceImpl implements PhysicalExamReportService {
    
    @Autowired
    private PhysicalExamReportMapper physicalExamReportMapper;
    
    @Override
    public List<PhysicalExamReport> getAllReports() {
        return physicalExamReportMapper.selectAll();
    }
    
    @Override
    public PageInfo<PhysicalExamReport> getReportsByPage(Integer userId, Integer doctorId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        PhysicalExamReport condition = new PhysicalExamReport();
        condition.setUserId(userId);
        condition.setDoctorId(doctorId);
        List<PhysicalExamReport> reports = physicalExamReportMapper.selectByCondition(condition);
        return new PageInfo<>(reports);
    }
    
    @Override
    public PhysicalExamReport getReportById(Integer reportId) {
        return physicalExamReportMapper.selectById(reportId);
    }
    
    @Override
    public void addReport(PhysicalExamReport report) {
        physicalExamReportMapper.insert(report);
    }
    
    @Override
    public void updateReport(PhysicalExamReport report) {
        physicalExamReportMapper.update(report);
    }
    
    @Override
    public void deleteReport(Integer reportId) {
        physicalExamReportMapper.deleteById(reportId);
    }
} 