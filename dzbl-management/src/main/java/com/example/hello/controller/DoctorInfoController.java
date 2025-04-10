package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.DoctorInfo;
import com.example.hello.service.DoctorInfoService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
public class DoctorInfoController {
    
    @Autowired
    private DoctorInfoService doctorInfoService;
    
    @GetMapping
    public Result<List<DoctorInfo>> getAllDoctors() {
        return Result.success(doctorInfoService.getAllDoctors());
    }
    
    @GetMapping(params = {"page", "pageSize"})
    public Result<PageInfo<DoctorInfo>> getDoctorsByPage(
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String introduction,
            @RequestParam(required = false) String doctorName,
            @RequestParam Integer page,
            @RequestParam Integer pageSize) {
        return Result.success(doctorInfoService.getDoctorsByPage(departmentId, title, introduction, doctorName, page, pageSize));
    }
    
    @GetMapping("/{doctorId}")
    public Result<DoctorInfo> getDoctorById(@PathVariable Integer doctorId) {
        return Result.success(doctorInfoService.getDoctorById(doctorId));
    }
    
    @PostMapping
    public Result<?> addDoctor(@RequestBody DoctorInfo doctor) {
        // 添加参数校验
        if (doctor.getDepartmentId() == null) {
            return Result.error("科室ID不能为空");
        }
        if (doctor.getTitle() == null || doctor.getTitle().trim().isEmpty()) {
            return Result.error("职称不能为空");
        }
        if (doctor.getLicenseNo() == null || doctor.getLicenseNo().trim().isEmpty()) {
            return Result.error("执业证书号不能为空");
        }
        if (doctor.getDoctorName() == null || doctor.getDoctorName().trim().isEmpty()) {
            return Result.error("医生姓名不能为空");
        }
        
        doctorInfoService.addDoctor(doctor);
        return Result.success(null);
    }
    
    @PutMapping("/{doctorId}")
    public Result<?> updateDoctor(@PathVariable Integer doctorId, @RequestBody DoctorInfo doctor) {
        doctor.setDoctorId(doctorId);
        doctorInfoService.updateDoctor(doctor);
        return Result.success(null);
    }
    
    @DeleteMapping("/{doctorId}")
    public Result<?> deleteDoctor(@PathVariable Integer doctorId) {
        doctorInfoService.deleteDoctor(doctorId);
        return Result.success(null);
    }
} 