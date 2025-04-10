package com.example.hello.service;

import com.example.hello.entity.DoctorSchedule;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface DoctorScheduleService {
    List<DoctorSchedule> getAllSchedules();
    PageInfo<DoctorSchedule> getSchedulesByPage(Integer doctorId, String scheduleType, String doctorName, String department, Integer page, Integer pageSize);
    DoctorSchedule getScheduleById(Integer scheduleId);
    void addSchedule(DoctorSchedule schedule);
    void updateSchedule(DoctorSchedule schedule);
    void deleteSchedule(Integer scheduleId);
} 