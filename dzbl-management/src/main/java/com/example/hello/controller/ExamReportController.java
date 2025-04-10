package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.ExamReport;
import com.example.hello.service.ExamReportService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exam-reports")
public class ExamReportController {
    
    @Autowired
    private ExamReportService examReportService;
    
    @GetMapping
    public Result<List<ExamReport>> getAllReports() {
        return Result.success(examReportService.getAllReports());
    }
    
    @GetMapping(params = {"page", "pageSize"})
    public Result<PageInfo<ExamReport>> getReportsByPage(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer doctorId,
            @RequestParam(required = false) String reportType,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<ExamReport> pageInfo = examReportService.getReportsByPage(userId, doctorId, reportType, page, pageSize);
        return Result.success(pageInfo);
    }
    
    @GetMapping("/{reportId}")
    public Result<ExamReport> getReportById(@PathVariable Integer reportId) {
        return Result.success(examReportService.getReportById(reportId));
    }
    
    @PostMapping
    public Result<?> addReport(@RequestBody ExamReport report) {
        // 添加参数校验
        if (report.getUserId() == null) {
            return Result.error("患者ID不能为空");
        }
        if (report.getDoctorId() == null) {
            return Result.error("医生ID不能为空");
        }
        if (report.getConclusion() == null || report.getConclusion().trim().isEmpty()) {
            return Result.error("诊断结果不能为空");
        }
        
        examReportService.addReport(report);
        return Result.success(null);
    }
    
    @PutMapping("/{reportId}")
    public Result<?> updateReport(@PathVariable Integer reportId, @RequestBody ExamReport report) {
        report.setReportId(reportId);
        examReportService.updateReport(report);
        return Result.success(null);
    }
    
    @DeleteMapping("/{reportId}")
    public Result<?> deleteReport(@PathVariable Integer reportId) {
        examReportService.deleteReport(reportId);
        return Result.success(null);
    }
} 