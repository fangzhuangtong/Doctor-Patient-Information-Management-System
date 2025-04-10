package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.OutpatientReport;
import com.example.hello.service.OutpatientReportService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/outpatient-reports")
public class OutpatientReportController {
    
    @Autowired
    private OutpatientReportService outpatientReportService;
    
    @GetMapping
    public Result<List<OutpatientReport>> getAllReports() {
        return Result.success(outpatientReportService.getAllReports());
    }
    
    @GetMapping(params = {"page", "pageSize"})
    public Result<PageInfo<OutpatientReport>> getReportsByPage(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer doctorId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<OutpatientReport> pageInfo = outpatientReportService.getReportsByPage(userId, doctorId, page, pageSize);
        return Result.success(pageInfo);
    }
    
    @GetMapping("/{reportId}")
    public Result<OutpatientReport> getReportById(@PathVariable Integer reportId) {
        return Result.success(outpatientReportService.getReportById(reportId));
    }
    
    @PostMapping
    public Result<?> addReport(@RequestBody OutpatientReport report) {
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
        
        outpatientReportService.addReport(report);
        return Result.success(null);
    }
    
    @PutMapping("/{reportId}")
    public Result<?> updateReport(@PathVariable Integer reportId, @RequestBody OutpatientReport report) {
        report.setReportId(reportId);
        outpatientReportService.updateReport(report);
        return Result.success(null);
    }
    
    @DeleteMapping("/{reportId}")
    public Result<?> deleteReport(@PathVariable Integer reportId) {
        outpatientReportService.deleteReport(reportId);
        return Result.success(null);
    }
} 