package com.example.hello.service.impl;

import com.example.hello.entity.OutpatientReport;
import com.example.hello.mapper.OutpatientReportMapper;
import com.example.hello.service.OutpatientReportService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutpatientReportServiceImpl implements OutpatientReportService {
    
    @Autowired
    private OutpatientReportMapper outpatientReportMapper;
    
    @Override
    public List<OutpatientReport> getAllReports() {
        return outpatientReportMapper.selectAll();
    }
    
    @Override
    public PageInfo<OutpatientReport> getReportsByPage(Integer userId, Integer doctorId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        OutpatientReport condition = new OutpatientReport();
        condition.setUserId(userId);
        condition.setDoctorId(doctorId);
        List<OutpatientReport> reports = outpatientReportMapper.selectByCondition(condition);
        return new PageInfo<>(reports);
    }
    
    @Override
    public OutpatientReport getReportById(Integer reportId) {
        return outpatientReportMapper.selectById(reportId);
    }
    
    @Override
    public void addReport(OutpatientReport report) {
        outpatientReportMapper.insert(report);
    }
    
    @Override
    public void updateReport(OutpatientReport report) {
        outpatientReportMapper.update(report);
    }
    
    @Override
    public void deleteReport(Integer reportId) {
        outpatientReportMapper.deleteById(reportId);
    }
} 