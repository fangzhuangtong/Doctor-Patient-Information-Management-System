package com.example.hello.mapper;

import com.example.hello.entity.OutpatientReport;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface OutpatientReportMapper {
    List<OutpatientReport> selectAll();
    List<OutpatientReport> selectByCondition(OutpatientReport report);
    OutpatientReport selectById(Integer reportId);
    int insert(OutpatientReport report);
    int update(OutpatientReport report);
    int deleteById(Integer reportId);
} 