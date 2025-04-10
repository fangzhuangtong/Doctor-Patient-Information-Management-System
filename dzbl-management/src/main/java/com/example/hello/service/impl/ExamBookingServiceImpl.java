package com.example.hello.service.impl;

import com.example.hello.entity.ExamBooking;
import com.example.hello.mapper.ExamBookingMapper;
import com.example.hello.service.ExamBookingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamBookingServiceImpl implements ExamBookingService {
    
    @Autowired
    private ExamBookingMapper examBookingMapper;
    
    @Override
    public List<ExamBooking> getAllExamBookings() {
        return examBookingMapper.selectAll();
    }
    
    @Override
    public PageInfo<ExamBooking> getExamBookingsByPage(Integer userId, String status, Integer doctorId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        ExamBooking condition = new ExamBooking();
        condition.setUserId(userId);
        condition.setStatus(status);
        condition.setDoctorId(doctorId);
        List<ExamBooking> examBookings = examBookingMapper.selectByCondition(condition);
        return new PageInfo<>(examBookings);
    }
    
    @Override
    public ExamBooking getExamBookingById(Integer examId) {
        return examBookingMapper.selectById(examId);
    }
    
    @Override
    public void addExamBooking(ExamBooking examBooking) {
        // 设置默认值
        if (examBooking.getStatus() == null) {
            examBooking.setStatus("待检查");
        }
        examBookingMapper.insert(examBooking);
    }
    
    @Override
    public void updateExamBooking(ExamBooking examBooking) {
        examBookingMapper.update(examBooking);
    }
    
    @Override
    public void deleteExamBooking(Integer examId) {
        examBookingMapper.deleteById(examId);
    }
} 