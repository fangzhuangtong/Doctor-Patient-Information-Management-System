package com.example.hello.mapper;

import com.example.hello.entity.PhysicalExamReport;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PhysicalExamReportMapper {
    List<PhysicalExamReport> selectAll();
    List<PhysicalExamReport> selectByCondition(PhysicalExamReport report);
    PhysicalExamReport selectById(Integer reportId);
    int insert(PhysicalExamReport report);
    int update(PhysicalExamReport report);
    int deleteById(Integer reportId);
} 