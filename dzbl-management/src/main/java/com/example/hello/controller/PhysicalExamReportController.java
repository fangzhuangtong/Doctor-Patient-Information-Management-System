package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.PhysicalExamReport;
import com.example.hello.service.PhysicalExamReportService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/physical-exam-reports")
public class PhysicalExamReportController {
    
    @Autowired
    private PhysicalExamReportService physicalExamReportService;
    
    @GetMapping
    public Result<List<PhysicalExamReport>> getAllReports() {
        return Result.success(physicalExamReportService.getAllReports());
    }
    
    @GetMapping(params = {"page", "pageSize"})
    public Result<PageInfo<PhysicalExamReport>> getReportsByPage(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer doctorId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<PhysicalExamReport> pageInfo = physicalExamReportService.getReportsByPage(userId, doctorId, page, pageSize);
        return Result.success(pageInfo);
    }
    
    @GetMapping("/{reportId}")
    public Result<PhysicalExamReport> getReportById(@PathVariable Integer reportId) {
        return Result.success(physicalExamReportService.getReportById(reportId));
    }
    
    @PostMapping
    public Result<?> addReport(@RequestBody PhysicalExamReport report) {
        // 添加参数校验
        if (report.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        if (report.getExamId() == null) {
            return Result.error("体检预约ID不能为空");
        }
        if (report.getResults() == null) {
            return Result.error("体检结果不能为空");
        }
        
        physicalExamReportService.addReport(report);
        return Result.success(null);
    }
    
    @PutMapping("/{reportId}")
    public Result<?> updateReport(@PathVariable Integer reportId, @RequestBody PhysicalExamReport report) {
        report.setReportId(reportId);
        physicalExamReportService.updateReport(report);
        return Result.success(null);
    }
    
    @DeleteMapping("/{reportId}")
    public Result<?> deleteReport(@PathVariable Integer reportId) {
        physicalExamReportService.deleteReport(reportId);
        return Result.success(null);
    }
} 