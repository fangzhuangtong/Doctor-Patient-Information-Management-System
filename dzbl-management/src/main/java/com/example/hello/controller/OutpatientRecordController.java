package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.OutpatientRecord;
import com.example.hello.service.OutpatientRecordService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/outpatient-records")
public class OutpatientRecordController {
    
    @Autowired
    private OutpatientRecordService outpatientRecordService;
    
    @GetMapping
    public Result<List<OutpatientRecord>> getAllRecords() {
        return Result.success(outpatientRecordService.getAllRecords());
    }
    
    @GetMapping(params = {"page", "pageSize"})
    public Result<PageInfo<OutpatientRecord>> getRecordsByPage(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer doctorId,
            @RequestParam(required = false) Integer reportId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<OutpatientRecord> pageInfo = outpatientRecordService.getRecordsByPage(userId, doctorId, reportId, page, pageSize);
        return Result.success(pageInfo);
    }
    
    @GetMapping("/{recordId}")
    public Result<OutpatientRecord> getRecordById(@PathVariable Integer recordId) {
        return Result.success(outpatientRecordService.getRecordById(recordId));
    }
    
    @PostMapping
    public Result<?> addRecord(@RequestBody OutpatientRecord record) {
        // 添加参数校验
        if (record.getRegisterId() == null) {
            return Result.error("挂号记录ID不能为空");
        }
        if (record.getUserId() == null) {
            return Result.error("患者ID不能为空");
        }
        if (record.getDoctorId() == null) {
            return Result.error("医生ID不能为空");
        }
        if (record.getChiefComplaint() == null || record.getChiefComplaint().trim().isEmpty()) {
            return Result.error("主诉不能为空");
        }
        if (record.getDiagnosis() == null || record.getDiagnosis().trim().isEmpty()) {
            return Result.error("诊断结果不能为空");
        }
        
        outpatientRecordService.addRecord(record);
        return Result.success(null);
    }
    
    @PutMapping("/{recordId}")
    public Result<?> updateRecord(@PathVariable Integer recordId, @RequestBody OutpatientRecord record) {
        record.setRecordId(recordId);
        outpatientRecordService.updateRecord(record);
        return Result.success(null);
    }
    
    @DeleteMapping("/{recordId}")
    public Result<?> deleteRecord(@PathVariable Integer recordId) {
        outpatientRecordService.deleteRecord(recordId);
        return Result.success(null);
    }
} 