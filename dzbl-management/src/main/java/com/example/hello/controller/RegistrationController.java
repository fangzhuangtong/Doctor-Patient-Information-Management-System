package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.Registration;
import com.example.hello.service.RegistrationService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registrations")
public class RegistrationController {
    
    @Autowired
    private RegistrationService registrationService;
    
    @GetMapping
    public Result<List<Registration>> getAllRegistrations() {
        return Result.success(registrationService.getAllRegistrations());
    }
    
    @GetMapping(params = {"page", "pageSize"})
    public Result<PageInfo<Registration>> getRegistrationsByPage(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer doctorId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Registration> pageInfo = registrationService.getRegistrationsByPage(userId, doctorId, status, page, pageSize);
        return Result.success(pageInfo);
    }
    
    @GetMapping("/{regId}")
    public Result<Registration> getRegistrationById(@PathVariable Integer regId) {
        return Result.success(registrationService.getRegistrationById(regId));
    }
    
    @PostMapping
    public Result<?> addRegistration(@RequestBody Registration registration) {
        // 添加参数校验
        if (registration.getUserId() == null) {
            return Result.error("患者ID不能为空");
        }
        if (registration.getDepartmentId() == null) {
            return Result.error("科室ID不能为空");
        }
        if (registration.getDoctorId() == null) {
            return Result.error("医生ID不能为空");
        }
        if (registration.getRegTime() == null) {
            return Result.error("预约时间不能为空");
        }
        
        registrationService.addRegistration(registration);
        return Result.success(null);
    }
    
    @PutMapping("/{regId}")
    public Result<?> updateRegistration(@PathVariable Integer regId, @RequestBody Registration registration) {
        registration.setRegId(regId);
        registrationService.updateRegistration(registration);
        return Result.success(null);
    }
    
    @DeleteMapping("/{regId}")
    public Result<?> deleteRegistration(@PathVariable Integer regId) {
        registrationService.deleteRegistration(regId);
        return Result.success(null);
    }
} 