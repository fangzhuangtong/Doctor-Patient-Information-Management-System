package com.example.hello.mapper;

import com.example.hello.entity.ReportAudit;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ReportAuditMapper {
    List<ReportAudit> selectAll();
    List<ReportAudit> selectByCondition(ReportAudit audit);
    ReportAudit selectById(Integer auditId);
    int insert(ReportAudit audit);
    int update(ReportAudit audit);
    int deleteById(Integer auditId);
} 