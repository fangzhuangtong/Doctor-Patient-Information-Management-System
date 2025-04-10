package com.example.hello.service.impl;

import com.example.hello.entity.InpatientReport;
import com.example.hello.mapper.InpatientReportMapper;
import com.example.hello.service.InpatientReportService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InpatientReportServiceImpl implements InpatientReportService {
    
    @Autowired
    private InpatientReportMapper inpatientReportMapper;
    
    @Override
    public List<InpatientReport> getAllReports() {
        return inpatientReportMapper.selectAll();
    }
    
    @Override
    public PageInfo<InpatientReport> getReportsByPage(Integer userId, Integer doctorId, String reportType, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        InpatientReport condition = new InpatientReport();
        condition.setUserId(userId);
        condition.setDoctorId(doctorId);
        condition.setReportType(reportType);
        List<InpatientReport> reports = inpatientReportMapper.selectByCondition(condition);
        return new PageInfo<>(reports);
    }
    
    @Override
    public InpatientReport getReportById(Integer reportId) {
        return inpatientReportMapper.selectById(reportId);
    }
    
    @Override
    public void addReport(InpatientReport report) {
        // 设置默认值
        if (report.getReportType() == null) {
            report.setReportType("检查");
        }
        inpatientReportMapper.insert(report);
    }
    
    @Override
    public void updateReport(InpatientReport report) {
        inpatientReportMapper.update(report);
    }
    
    @Override
    public void deleteReport(Integer reportId) {
        inpatientReportMapper.deleteById(reportId);
    }
} 