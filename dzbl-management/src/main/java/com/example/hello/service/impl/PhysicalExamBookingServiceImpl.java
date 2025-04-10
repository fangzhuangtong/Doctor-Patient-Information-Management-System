package com.example.hello.service.impl;

import com.example.hello.entity.PhysicalExamBooking;
import com.example.hello.mapper.PhysicalExamBookingMapper;
import com.example.hello.service.PhysicalExamBookingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhysicalExamBookingServiceImpl implements PhysicalExamBookingService {
    
    @Autowired
    private PhysicalExamBookingMapper physicalExamBookingMapper;
    
    @Override
    public List<PhysicalExamBooking> getAllBookings() {
        return physicalExamBookingMapper.selectAll();
    }
    
    @Override
    public PageInfo<PhysicalExamBooking> getBookingsByPage(Integer userId, String status, Integer doctorId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        PhysicalExamBooking condition = new PhysicalExamBooking();
        condition.setUserId(userId);
        condition.setStatus(status);
        condition.setDoctorId(doctorId);
        List<PhysicalExamBooking> bookings = physicalExamBookingMapper.selectByCondition(condition);
        return new PageInfo<>(bookings);
    }
    
    @Override
    public PhysicalExamBooking getBookingById(Integer examId) {
        return physicalExamBookingMapper.selectById(examId);
    }
    
    @Override
    public void addBooking(PhysicalExamBooking booking) {
        // 设置默认值
        if (booking.getPackageType() == null) {
            booking.setPackageType("基础");
        }
        if (booking.getStatus() == null) {
            booking.setStatus("待体检");
        }
        physicalExamBookingMapper.insert(booking);
    }
    
    @Override
    public void updateBooking(PhysicalExamBooking booking) {
        physicalExamBookingMapper.update(booking);
    }
    
    @Override
    public void deleteBooking(Integer examId) {
        physicalExamBookingMapper.deleteById(examId);
    }
} 