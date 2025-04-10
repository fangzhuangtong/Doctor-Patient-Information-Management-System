package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.InpatientRecord;
import com.example.hello.service.InpatientRecordService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inpatient-records")
public class InpatientRecordController {
    
    @Autowired
    private InpatientRecordService inpatientRecordService;
    
    @GetMapping
    public Result<List<InpatientRecord>> getAllRecords() {
        return Result.success(inpatientRecordService.getAllRecords());
    }
    
    @GetMapping(params = {"page", "pageSize"})
    public Result<PageInfo<InpatientRecord>> getRecordsByPage(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer doctorId,
            @RequestParam(required = false) Integer reportId,
            @RequestParam Integer page,
            @RequestParam Integer pageSize) {
        return Result.success(inpatientRecordService.getRecordsByPage(userId, doctorId, reportId, page, pageSize));
    }
    
    @GetMapping("/{recordId}")
    public Result<InpatientRecord> getRecordById(@PathVariable Integer recordId) {
        return Result.success(inpatientRecordService.getRecordById(recordId));
    }
    
    @PostMapping
    public Result<?> addRecord(@RequestBody InpatientRecord record) {
        // 添加参数校验
        if (record.getHospitalId() == null) {
            return Result.error("住院记录ID不能为空");
        }
        if (record.getUserId() == null) {
            return Result.error("患者ID不能为空");
        }
        if (record.getDoctorId() == null) {
            return Result.error("医生ID不能为空");
        }
        if (record.getAdmissionDiagnosis() == null || record.getAdmissionDiagnosis().trim().isEmpty()) {
            return Result.error("入院诊断不能为空");
        }
        
        inpatientRecordService.addRecord(record);
        return Result.success(null);
    }
    
    @PutMapping("/{recordId}")
    public Result<?> updateRecord(@PathVariable Integer recordId, @RequestBody InpatientRecord record) {
        record.setRecordId(recordId);
        inpatientRecordService.updateRecord(record);
        return Result.success(null);
    }
    
    @DeleteMapping("/{recordId}")
    public Result<?> deleteRecord(@PathVariable Integer recordId) {
        inpatientRecordService.deleteRecord(recordId);
        return Result.success(null);
    }
} 