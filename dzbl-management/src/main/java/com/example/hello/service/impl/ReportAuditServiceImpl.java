package com.example.hello.service.impl;

import com.example.hello.entity.ReportAudit;
import com.example.hello.mapper.ReportAuditMapper;
import com.example.hello.service.ReportAuditService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportAuditServiceImpl implements ReportAuditService {
    
    @Autowired
    private ReportAuditMapper reportAuditMapper;
    
    @Override
    public List<ReportAudit> getAllAudits() {
        return reportAuditMapper.selectAll();
    }
    
    @Override
    public PageInfo<ReportAudit> getAuditsByPage(String reportType, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        ReportAudit condition = new ReportAudit();
        condition.setReportType(reportType);
        List<ReportAudit> audits = reportAuditMapper.selectByCondition(condition);
        return new PageInfo<>(audits);
    }
    
    @Override
    public ReportAudit getAuditById(Integer auditId) {
        return reportAuditMapper.selectById(auditId);
    }
    
    @Override
    public void addAudit(ReportAudit audit) {
        reportAuditMapper.insert(audit);
    }
    
    @Override
    public void updateAudit(ReportAudit audit) {
        reportAuditMapper.update(audit);
    }
    
    @Override
    public void deleteAudit(Integer auditId) {
        reportAuditMapper.deleteById(auditId);
    }
} 