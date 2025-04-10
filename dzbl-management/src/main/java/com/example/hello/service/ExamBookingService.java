package com.example.hello.service;

import com.example.hello.entity.ExamBooking;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface ExamBookingService {
    List<ExamBooking> getAllExamBookings();
    PageInfo<ExamBooking> getExamBookingsByPage(Integer userId, String status, Integer doctorId, Integer page, Integer pageSize);
    ExamBooking getExamBookingById(Integer examId);
    void addExamBooking(ExamBooking examBooking);
    void updateExamBooking(ExamBooking examBooking);
    void deleteExamBooking(Integer examId);
} 