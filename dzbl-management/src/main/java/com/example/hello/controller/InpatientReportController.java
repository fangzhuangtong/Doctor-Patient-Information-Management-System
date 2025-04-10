package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.InpatientReport;
import com.example.hello.service.InpatientReportService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inpatient-reports")
public class InpatientReportController {
    
    @Autowired
    private InpatientReportService inpatientReportService;
    
    @GetMapping
    public Result<List<InpatientReport>> getAllReports() {
        return Result.success(inpatientReportService.getAllReports());
    }
    
    @GetMapping(params = {"page", "pageSize"})
    public Result<PageInfo<InpatientReport>> getReportsByPage(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer doctorId,
            @RequestParam(required = false) String reportType,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<InpatientReport> pageInfo = inpatientReportService.getReportsByPage(userId, doctorId, reportType, page, pageSize);
        return Result.success(pageInfo);
    }
    
    @GetMapping("/{reportId}")
    public Result<InpatientReport> getReportById(@PathVariable Integer reportId) {
        return Result.success(inpatientReportService.getReportById(reportId));
    }
    
    @PostMapping
    public Result<?> addReport(@RequestBody InpatientReport report) {
        // 添加参数校验
        if (report.getUserId() == null) {
            return Result.error("患者ID不能为空");
        }
        if (report.getDoctorId() == null) {
            return Result.error("医生ID不能为空");
        }
        if (report.getDiagnosis() == null || report.getDiagnosis().trim().isEmpty()) {
            return Result.error("诊断结果不能为空");
        }
        
        inpatientReportService.addReport(report);
        return Result.success(null);
    }
    
    @PutMapping("/{reportId}")
    public Result<?> updateReport(@PathVariable Integer reportId, @RequestBody InpatientReport report) {
        report.setReportId(reportId);
        inpatientReportService.updateReport(report);
        return Result.success(null);
    }
    
    @DeleteMapping("/{reportId}")
    public Result<?> deleteReport(@PathVariable Integer reportId) {
        inpatientReportService.deleteReport(reportId);
        return Result.success(null);
    }
} 