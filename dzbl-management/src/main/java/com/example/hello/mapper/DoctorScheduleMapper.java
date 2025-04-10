package com.example.hello.mapper;

import com.example.hello.entity.DoctorSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DoctorScheduleMapper {
    List<DoctorSchedule> selectAll();
    List<DoctorSchedule> selectByCondition(Map<String, Object> params);
    DoctorSchedule selectById(@Param("scheduleId") Integer scheduleId);
    void insert(DoctorSchedule schedule);
    void update(DoctorSchedule schedule);
    void deleteById(@Param("scheduleId") Integer scheduleId);
} 