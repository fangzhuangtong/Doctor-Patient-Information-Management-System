package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.PhysicalExamBooking;
import com.example.hello.service.PhysicalExamBookingService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/physical-exam-bookings")
public class PhysicalExamBookingController {
    
    @Autowired
    private PhysicalExamBookingService physicalExamBookingService;
    
    @GetMapping
    public Result<List<PhysicalExamBooking>> getAllBookings() {
        return Result.success(physicalExamBookingService.getAllBookings());
    }
    
    @GetMapping(params = {"page", "pageSize"})
    public Result<PageInfo<PhysicalExamBooking>> getBookingsByPage(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer doctorId,
            @RequestParam Integer page,
            @RequestParam Integer pageSize) {
        return Result.success(physicalExamBookingService.getBookingsByPage(userId, status, doctorId, page, pageSize));
    }
    
    @GetMapping("/{examId}")
    public Result<PhysicalExamBooking> getBookingById(@PathVariable Integer examId) {
        return Result.success(physicalExamBookingService.getBookingById(examId));
    }
    
    @PostMapping
    public Result<?> addBooking(@RequestBody PhysicalExamBooking booking) {
        // 添加参数校验
        if (booking.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        if (booking.getExamTime() == null) {
            return Result.error("体检时间不能为空");
        }
        if (booking.getInstitution() == null || booking.getInstitution().trim().isEmpty()) {
            return Result.error("体检机构不能为空");
        }
        if (booking.getDoctorId() == null) {
            return Result.error("医生ID不能为空");
        }
        
        physicalExamBookingService.addBooking(booking);
        return Result.success(null);
    }
    
    @PutMapping("/{examId}")
    public Result<?> updateBooking(@PathVariable Integer examId, @RequestBody PhysicalExamBooking booking) {
        booking.setExamId(examId);
        physicalExamBookingService.updateBooking(booking);
        return Result.success(null);
    }
    
    @DeleteMapping("/{examId}")
    public Result<?> deleteBooking(@PathVariable Integer examId) {
        physicalExamBookingService.deleteBooking(examId);
        return Result.success(null);
    }
} 