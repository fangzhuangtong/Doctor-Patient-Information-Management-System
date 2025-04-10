package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.HealthRecord;
import com.example.hello.service.HealthRecordService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/health-records")
public class HealthRecordController {
    
    @Autowired
    private HealthRecordService healthRecordService;
    
    @GetMapping
    public Result<List<HealthRecord>> getAllRecords() {
        return Result.success(healthRecordService.getAllRecords());
    }
    
    @GetMapping(params = {"page", "pageSize"})
    public Result<PageInfo<HealthRecord>> getRecordsByPage(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String bloodType,
            @RequestParam Integer page,
            @RequestParam Integer pageSize) {
        return Result.success(healthRecordService.getRecordsByPage(userId, bloodType, page, pageSize));
    }
    
    @GetMapping("/{recordId}")
    public Result<HealthRecord> getRecordById(@PathVariable Integer recordId) {
        return Result.success(healthRecordService.getRecordById(recordId));
    }
    
    @PostMapping
    public Result<?> addRecord(@RequestBody HealthRecord record) {
        if (record.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        if (record.getHeight() != null && record.getHeight().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("身高必须大于0");
        }
        if (record.getWeight() != null && record.getWeight().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("体重必须大于0");
        }
        
        healthRecordService.addRecord(record);
        return Result.success(null);
    }
    
    @PutMapping("/{recordId}")
    public Result<?> updateRecord(@PathVariable Integer recordId, @RequestBody HealthRecord record) {
        record.setRecordId(recordId);
        healthRecordService.updateRecord(record);
        return Result.success(null);
    }
    
    @DeleteMapping("/{recordId}")
    public Result<?> deleteRecord(@PathVariable Integer recordId) {
        healthRecordService.deleteRecord(recordId);
        return Result.success(null);
    }
    
    @GetMapping("/user/{userId}")
    public Result<List<HealthRecord>> getRecordsByUserId(@PathVariable Integer userId) {
        try {
            List<HealthRecord> records = healthRecordService.getRecordsByUserId(userId);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
} 