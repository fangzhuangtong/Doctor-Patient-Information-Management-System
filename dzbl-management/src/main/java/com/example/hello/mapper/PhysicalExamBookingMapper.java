package com.example.hello.mapper;

import com.example.hello.entity.PhysicalExamBooking;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PhysicalExamBookingMapper {
    List<PhysicalExamBooking> selectAll();
    List<PhysicalExamBooking> selectByCondition(PhysicalExamBooking booking);
    PhysicalExamBooking selectById(Integer examId);
    int insert(PhysicalExamBooking booking);
    int update(PhysicalExamBooking booking);
    int deleteById(Integer examId);
} 