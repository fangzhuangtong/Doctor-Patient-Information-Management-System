package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.DoctorSchedule;
import com.example.hello.service.DoctorScheduleService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctor-schedules")
public class DoctorScheduleController {
    
    @Autowired
    private DoctorScheduleService doctorScheduleService;
    
    @GetMapping
    public Result<List<DoctorSchedule>> getAllSchedules() {
        return Result.success(doctorScheduleService.getAllSchedules());
    }
    
    @GetMapping(params = {"page", "pageSize"})
    public Result<PageInfo<DoctorSchedule>> getSchedulesByPage(
            @RequestParam(required = false) Integer doctorId,
            @RequestParam(required = false) String scheduleType,
            @RequestParam(required = false) String doctorName,
            @RequestParam(required = false) String department,
            @RequestParam Integer page,
            @RequestParam Integer pageSize) {
        return Result.success(doctorScheduleService.getSchedulesByPage(doctorId, scheduleType, doctorName, department, page, pageSize));
    }
    
    @GetMapping("/{scheduleId}")
    public Result<DoctorSchedule> getScheduleById(@PathVariable Integer scheduleId) {
        return Result.success(doctorScheduleService.getScheduleById(scheduleId));
    }
    
    @PostMapping
    public Result<?> addSchedule(@RequestBody DoctorSchedule schedule) {
        // 添加参数校验
        if (schedule.getDoctorId() == null) {
            return Result.error("医生ID不能为空");
        }
        if (schedule.getScheduleType() == null || schedule.getScheduleType().trim().isEmpty()) {
            return Result.error("排班类型不能为空");
        }
        if (schedule.getScheduleDate() == null) {
            return Result.error("出诊日期不能为空");
        }
        if (schedule.getTimeRange() == null || schedule.getTimeRange().trim().isEmpty()) {
            return Result.error("时间段不能为空");
        }
        if (schedule.getMaxPatient() == null || schedule.getMaxPatient() <= 0) {
            return Result.error("最大接诊人数必须大于0");
        }
        if (schedule.getDoctorName() == null || schedule.getDoctorName().trim().isEmpty()) {
            return Result.error("医生姓名不能为空");
        }
        if (schedule.getDepartment() == null || schedule.getDepartment().trim().isEmpty()) {
            return Result.error("科室不能为空");
        }
        
        doctorScheduleService.addSchedule(schedule);
        return Result.success(null);
    }
    
    @PutMapping("/{scheduleId}")
    public Result<?> updateSchedule(@PathVariable Integer scheduleId, @RequestBody DoctorSchedule schedule) {
        schedule.setScheduleId(scheduleId);
        doctorScheduleService.updateSchedule(schedule);
        return Result.success(null);
    }
    
    @DeleteMapping("/{scheduleId}")
    public Result<?> deleteSchedule(@PathVariable Integer scheduleId) {
        doctorScheduleService.deleteSchedule(scheduleId);
        return Result.success(null);
    }
} 