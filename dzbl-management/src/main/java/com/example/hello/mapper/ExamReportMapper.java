package com.example.hello.mapper;

import com.example.hello.entity.ExamReport;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ExamReportMapper {
    List<ExamReport> selectAll();
    List<ExamReport> selectByCondition(ExamReport report);
    ExamReport selectById(Integer reportId);
    int insert(ExamReport report);
    int update(ExamReport report);
    int deleteById(Integer reportId);
} 