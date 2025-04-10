package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.Hospitalization;
import com.example.hello.service.HospitalizationService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hospitalizations")
public class HospitalizationController {
    
    @Autowired
    private HospitalizationService hospitalizationService;
    
    @GetMapping
    public Result<List<Hospitalization>> getAllHospitalizations() {
        return Result.success(hospitalizationService.getAllHospitalizations());
    }
    
    @GetMapping(params = {"page", "pageSize"})
    public Result<PageInfo<Hospitalization>> getHospitalizationsByPage(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer doctorId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Hospitalization> pageInfo = hospitalizationService.getHospitalizationsByPage(userId, doctorId, status, page, pageSize);
        return Result.success(pageInfo);
    }
    
    @GetMapping("/{hosId}")
    public Result<Hospitalization> getHospitalizationById(@PathVariable Integer hosId) {
        return Result.success(hospitalizationService.getHospitalizationById(hosId));
    }
    
    @PostMapping
    public Result<?> addHospitalization(@RequestBody Hospitalization hospitalization) {
        if (hospitalization.getUserId() == null) {
            return Result.error("患者ID不能为空");
        }
        if (hospitalization.getDoctorId() == null) {
            return Result.error("医生ID不能为空");
        }
        if (hospitalization.getAdmitTime() == null) {
            return Result.error("入院时间不能为空");
        }
        if (hospitalization.getBedNumber() == null || hospitalization.getBedNumber().trim().isEmpty()) {
            return Result.error("病床号不能为空");
        }
        if (hospitalization.getStatus() == null || hospitalization.getStatus().trim().isEmpty()) {
            return Result.error("住院状态不能为空");
        }
        
        hospitalizationService.addHospitalization(hospitalization);
        return Result.success(null);
    }
    
    @PutMapping("/{hosId}")
    public Result<?> updateHospitalization(@PathVariable Integer hosId, @RequestBody Hospitalization hospitalization) {
        hospitalization.setHosId(hosId);
        hospitalizationService.updateHospitalization(hospitalization);
        return Result.success(null);
    }
    
    @DeleteMapping("/{hosId}")
    public Result<?> deleteHospitalization(@PathVariable Integer hosId) {
        hospitalizationService.deleteHospitalization(hosId);
        return Result.success(null);
    }
} 