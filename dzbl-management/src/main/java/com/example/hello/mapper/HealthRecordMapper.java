package com.example.hello.mapper;

import com.example.hello.entity.HealthRecord;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface HealthRecordMapper {
    List<HealthRecord> selectAll();
    List<HealthRecord> selectByCondition(HealthRecord record);
    HealthRecord selectById(Integer recordId);
    int insert(HealthRecord record);
    int update(HealthRecord record);
    int deleteById(Integer recordId);
    List<HealthRecord> selectByUserId(Integer userId);
} 