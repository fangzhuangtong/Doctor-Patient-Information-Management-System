package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.ExamBooking;
import com.example.hello.service.ExamBookingService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exam-bookings")
public class ExamBookingController {
    
    @Autowired
    private ExamBookingService examBookingService;
    
    @GetMapping
    public Result<List<ExamBooking>> getAllExamBookings() {
        return Result.success(examBookingService.getAllExamBookings());
    }
    
    @GetMapping(params = {"page", "pageSize"})
    public Result<PageInfo<ExamBooking>> getExamBookingsByPage(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer doctorId,
            @RequestParam Integer page,
            @RequestParam Integer pageSize) {
        return Result.success(examBookingService.getExamBookingsByPage(userId, status, doctorId, page, pageSize));
    }
    
    @GetMapping("/{examId}")
    public Result<ExamBooking> getExamBookingById(@PathVariable Integer examId) {
        return Result.success(examBookingService.getExamBookingById(examId));
    }
    
    @PostMapping
    public Result<?> addExamBooking(@RequestBody ExamBooking examBooking) {
        // 添加参数校验
        if (examBooking.getUserId() == null) {
            return Result.error("患者ID不能为空");
        }
        if (examBooking.getExamType() == null || examBooking.getExamType().trim().isEmpty()) {
            return Result.error("检查类型不能为空");
        }
        if (examBooking.getScheduleTime() == null) {
            return Result.error("预约时间不能为空");
        }
        if (examBooking.getDoctorId() == null) {
            return Result.error("医生ID不能为空");
        }
        
        examBookingService.addExamBooking(examBooking);
        return Result.success(null);
    }
    
    @PutMapping("/{examId}")
    public Result<?> updateExamBooking(@PathVariable Integer examId, @RequestBody ExamBooking examBooking) {
        examBooking.setExamId(examId);
        examBookingService.updateExamBooking(examBooking);
        return Result.success(null);
    }
    
    @DeleteMapping("/{examId}")
    public Result<?> deleteExamBooking(@PathVariable Integer examId) {
        examBookingService.deleteExamBooking(examId);
        return Result.success(null);
    }
} 