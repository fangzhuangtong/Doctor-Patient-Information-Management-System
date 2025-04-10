package com.example.hello.service.impl;

import com.example.hello.entity.DoctorSchedule;
import com.example.hello.mapper.DoctorScheduleMapper;
import com.example.hello.service.DoctorScheduleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DoctorScheduleServiceImpl implements DoctorScheduleService {
    
    @Autowired
    private DoctorScheduleMapper doctorScheduleMapper;
    
    @Override
    public List<DoctorSchedule> getAllSchedules() {
        return doctorScheduleMapper.selectAll();
    }
    
    @Override
    public PageInfo<DoctorSchedule> getSchedulesByPage(Integer doctorId, String scheduleType, 
            String doctorName, String department, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        
        Map<String, Object> params = new HashMap<>();
        params.put("doctorId", doctorId);
        params.put("scheduleType", scheduleType);
        params.put("doctorName", doctorName);
        params.put("department", department);
        
        List<DoctorSchedule> schedules = doctorScheduleMapper.selectByCondition(params);
        return new PageInfo<>(schedules);
    }
    
    @Override
    public DoctorSchedule getScheduleById(Integer scheduleId) {
        return doctorScheduleMapper.selectById(scheduleId);
    }
    
    @Override
    public void addSchedule(DoctorSchedule schedule) {
        doctorScheduleMapper.insert(schedule);
    }
    
    @Override
    public void updateSchedule(DoctorSchedule schedule) {
        doctorScheduleMapper.update(schedule);
    }
    
    @Override
    public void deleteSchedule(Integer scheduleId) {
        doctorScheduleMapper.deleteById(scheduleId);
    }
} 