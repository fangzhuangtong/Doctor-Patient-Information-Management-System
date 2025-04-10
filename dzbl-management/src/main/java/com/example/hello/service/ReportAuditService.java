package com.example.hello.service;

import com.example.hello.entity.ReportAudit;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface ReportAuditService {
    List<ReportAudit> getAllAudits();
    PageInfo<ReportAudit> getAuditsByPage(String reportType, Integer page, Integer pageSize);
    ReportAudit getAuditById(Integer auditId);
    void addAudit(ReportAudit audit);
    void updateAudit(ReportAudit audit);
    void deleteAudit(Integer auditId);
} 