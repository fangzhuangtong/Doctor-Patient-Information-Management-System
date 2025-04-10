package com.example.hello.mapper;

import com.example.hello.entity.InpatientRecord;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface InpatientRecordMapper {
    List<InpatientRecord> selectAll();
    List<InpatientRecord> selectByCondition(InpatientRecord record);
    InpatientRecord selectById(Integer recordId);
    int insert(InpatientRecord record);
    int update(InpatientRecord record);
    int deleteById(Integer recordId);
} 