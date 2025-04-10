package com.example.hello.mapper;

import com.example.hello.entity.InpatientReport;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface InpatientReportMapper {
    List<InpatientReport> selectAll();
    List<InpatientReport> selectByCondition(InpatientReport report);
    InpatientReport selectById(Integer reportId);
    int insert(InpatientReport report);
    int update(InpatientReport report);
    int deleteById(Integer reportId);
} 