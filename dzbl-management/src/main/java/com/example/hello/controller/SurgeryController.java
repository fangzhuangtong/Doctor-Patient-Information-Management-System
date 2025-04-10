package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.Surgery;
import com.example.hello.service.SurgeryService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/surgeries")
public class SurgeryController {
    
    @Autowired
    private SurgeryService surgeryService;
    
    @GetMapping
    public Result<List<Surgery>> getAllSurgeries() {
        return Result.success(surgeryService.getAllSurgeries());
    }
    
    @GetMapping(params = {"page", "pageSize"})
    public Result<PageInfo<Surgery>> getSurgeriesByPage(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String status,
            @RequestParam Integer page,
            @RequestParam Integer pageSize) {
        return Result.success(surgeryService.getSurgeriesByPage(userId, status, page, pageSize));
    }
    
    @GetMapping("/{surgeryId}")
    public Result<Surgery> getSurgeryById(@PathVariable Integer surgeryId) {
        return Result.success(surgeryService.getSurgeryById(surgeryId));
    }
    
    @PostMapping
    public Result<?> addSurgery(@RequestBody Surgery surgery) {
        // 添加参数校验
        if (surgery.getUserId() == null) {
            return Result.error("患者ID不能为空");
        }
        if (surgery.getSurgeryName() == null || surgery.getSurgeryName().trim().isEmpty()) {
            return Result.error("手术名称不能为空");
        }
        if (surgery.getSurgeonId() == null) {
            return Result.error("主刀医生ID不能为空");
        }
        if (surgery.getSurgeryTime() == null) {
            return Result.error("手术时间不能为空");
        }
        
        surgeryService.addSurgery(surgery);
        return Result.success(null);
    }
    
    @PutMapping("/{surgeryId}")
    public Result<?> updateSurgery(@PathVariable Integer surgeryId, @RequestBody Surgery surgery) {
        surgery.setSurgeryId(surgeryId);
        surgeryService.updateSurgery(surgery);
        return Result.success(null);
    }
    
    @DeleteMapping("/{surgeryId}")
    public Result<?> deleteSurgery(@PathVariable Integer surgeryId) {
        surgeryService.deleteSurgery(surgeryId);
        return Result.success(null);
    }
} 