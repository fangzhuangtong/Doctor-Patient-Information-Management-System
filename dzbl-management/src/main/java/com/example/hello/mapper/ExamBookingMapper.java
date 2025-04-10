package com.example.hello.mapper;

import com.example.hello.entity.ExamBooking;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ExamBookingMapper {
    List<ExamBooking> selectAll();
    List<ExamBooking> selectByCondition(ExamBooking examBooking);
    ExamBooking selectById(Integer examId);
    int insert(ExamBooking examBooking);
    int update(ExamBooking examBooking);
    int deleteById(Integer examId);
} 