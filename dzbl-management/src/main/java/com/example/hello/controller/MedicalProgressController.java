package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.MedicalProgress;
import com.example.hello.service.MedicalProgressService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medical-progresses")
public class MedicalProgressController {
    
    @Autowired
    private MedicalProgressService medicalProgressService;
    
    @GetMapping
    public Result<List<MedicalProgress>> getAllProgresses() {
        return Result.success(medicalProgressService.getAllProgresses());
    }
    
    @GetMapping(params = {"page", "pageSize"})
    public Result<PageInfo<MedicalProgress>> getProgressesByPage(
            @RequestParam(required = false) String recordType,
            @RequestParam(required = false) Integer doctorId,
            @RequestParam Integer page,
            @RequestParam Integer pageSize) {
        return Result.success(medicalProgressService.getProgressesByPage(recordType, doctorId, page, pageSize));
    }
    
    @GetMapping("/{reportId}")
    public Result<MedicalProgress> getProgressById(@PathVariable Integer reportId) {
        return Result.success(medicalProgressService.getProgressById(reportId));
    }
    
    @PostMapping
    public Result<?> addProgress(@RequestBody MedicalProgress progress) {
        // 添加参数校验
        if (progress.getRecordType() == null || progress.getRecordType().trim().isEmpty()) {
            return Result.error("记录类型不能为空");
        }
        if (progress.getRecordId() == null) {
            return Result.error("关联病历ID不能为空");
        }
        if (progress.getContent() == null || progress.getContent().trim().isEmpty()) {
            return Result.error("记录内容不能为空");
        }
        if (progress.getDoctorId() == null) {
            return Result.error("医生ID不能为空");
        }
        if (progress.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        
        medicalProgressService.addProgress(progress);
        return Result.success(null);
    }
    
    @PutMapping("/{reportId}")
    public Result<?> updateProgress(@PathVariable Integer reportId, @RequestBody MedicalProgress progress) {
        progress.setReportId(reportId);
        medicalProgressService.updateProgress(progress);
        return Result.success(null);
    }
    
    @DeleteMapping("/{reportId}")
    public Result<?> deleteProgress(@PathVariable Integer reportId) {
        medicalProgressService.deleteProgress(reportId);
        return Result.success(null);
    }
} 