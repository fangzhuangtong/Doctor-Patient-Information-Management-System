package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.Consultation;
import com.example.hello.service.ConsultationService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultations")
public class ConsultationController {
    
    @Autowired
    private ConsultationService consultationService;
    
    @GetMapping
    public Result<List<Consultation>> getAllConsultations() {
        return Result.success(consultationService.getAllConsultations());
    }
    
    @GetMapping("/search")
    public Result<PageInfo<Consultation>> getConsultationsByPage(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer doctorId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return Result.success(consultationService.getConsultationsByPage(userId, doctorId, status, page, pageSize));
    }
    
    @GetMapping("/{consultId}")
    public Result<Consultation> getConsultationById(@PathVariable Integer consultId) {
        return Result.success(consultationService.getConsultationById(consultId));
    }
    
    @PostMapping
    public Result<?> addConsultation(@RequestBody Consultation consultation) {
        // 添加参数校验
        if (consultation.getUserId() == null) {
            return Result.error("咨询人ID不能为空");
        }
        if (consultation.getDoctorId() == null) {
            return Result.error("接收人ID不能为空");
        }
        if (consultation.getContent() == null || consultation.getContent().trim().isEmpty()) {
            return Result.error("咨询内容不能为空");
        }
        
        consultationService.addConsultation(consultation);
        return Result.success(null);
    }
    
    @PutMapping("/{consultId}")
    public Result<?> updateConsultation(@PathVariable Integer consultId, @RequestBody Consultation consultation) {
        consultation.setConsultId(consultId);
        consultationService.updateConsultation(consultation);
        return Result.success(null);
    }
    
    @DeleteMapping("/{consultId}")
    public Result<?> deleteConsultation(@PathVariable Integer consultId) {
        consultationService.deleteConsultation(consultId);
        return Result.success(null);
    }

    @GetMapping("/user/{userId}")
    public Result<List<Consultation>> getConsultationsByUserId(@PathVariable Integer userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        Consultation condition = new Consultation();
        condition.setUserId(userId);
        List<Consultation> consultations = consultationService.getConsultationsByCondition(condition);
        return Result.success(consultations);
    }

    @GetMapping("/doctor/{doctorId}")
    public Result<List<Consultation>> getConsultationsByDoctorId(@PathVariable Integer doctorId) {
        if (doctorId == null) {
            return Result.error("医生ID不能为空");
        }
        Consultation condition = new Consultation();
        condition.setDoctorId(doctorId);
        List<Consultation> consultations = consultationService.getConsultationsByCondition(condition);
        return Result.success(consultations);
    }

    @GetMapping("/patient/{patientId}")
    public Result<List<Consultation>> getConsultationsByPatientId(@PathVariable Integer patientId) {
        if (patientId == null) {
            return Result.error("患者ID不能为空");
        }
        Consultation condition = new Consultation();
        condition.setUserId(patientId);  // 使用userId字段存储patientId
        List<Consultation> consultations = consultationService.getConsultationsByCondition(condition);
        return Result.success(consultations);
    }
} 