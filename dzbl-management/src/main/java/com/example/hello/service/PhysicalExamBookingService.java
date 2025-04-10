package com.example.hello.service;

import com.example.hello.entity.PhysicalExamBooking;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface PhysicalExamBookingService {
    List<PhysicalExamBooking> getAllBookings();
    PageInfo<PhysicalExamBooking> getBookingsByPage(Integer userId, String status, Integer doctorId, Integer page, Integer pageSize);
    PhysicalExamBooking getBookingById(Integer examId);
    void addBooking(PhysicalExamBooking booking);
    void updateBooking(PhysicalExamBooking booking);
    void deleteBooking(Integer examId);
} 