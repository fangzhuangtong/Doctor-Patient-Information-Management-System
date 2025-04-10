package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.ReportAudit;
import com.example.hello.service.ReportAuditService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report-audits")
public class ReportAuditController {
    
    @Autowired
    private ReportAuditService reportAuditService;
    
    @GetMapping
    public Result<List<ReportAudit>> getAllAudits() {
        return Result.success(reportAuditService.getAllAudits());
    }
    
    @GetMapping(params = {"page", "pageSize"})
    public Result<PageInfo<ReportAudit>> getAuditsByPage(
            @RequestParam(required = false) String reportType,
            @RequestParam Integer page,
            @RequestParam Integer pageSize) {
        return Result.success(reportAuditService.getAuditsByPage(reportType, page, pageSize));
    }
    
    @GetMapping("/{auditId}")
    public Result<ReportAudit> getAuditById(@PathVariable Integer auditId) {
        return Result.success(reportAuditService.getAuditById(auditId));
    }
    
    @PostMapping
    public Result<?> addAudit(@RequestBody ReportAudit audit) {
        // 添加参数校验
        if (audit.getReportType() == null || audit.getReportType().trim().isEmpty()) {
            return Result.error("报告类型不能为空");
        }
        if (audit.getReportId() == null) {
            return Result.error("关联报告ID不能为空");
        }
        if (audit.getAuditResult() == null || audit.getAuditResult().trim().isEmpty()) {
            return Result.error("审核结果不能为空");
        }
        if (audit.getDoctorId() == null) {
            return Result.error("医生ID不能为空");
        }
        
        reportAuditService.addAudit(audit);
        return Result.success(null);
    }
    
    @PutMapping("/{auditId}")
    public Result<?> updateAudit(@PathVariable Integer auditId, @RequestBody ReportAudit audit) {
        audit.setAuditId(auditId);
        reportAuditService.updateAudit(audit);
        return Result.success(null);
    }
    
    @DeleteMapping("/{auditId}")
    public Result<?> deleteAudit(@PathVariable Integer auditId) {
        reportAuditService.deleteAudit(auditId);
        return Result.success(null);
    }
} 