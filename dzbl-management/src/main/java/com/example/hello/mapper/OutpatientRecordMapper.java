package com.example.hello.mapper;

import com.example.hello.entity.OutpatientRecord;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface OutpatientRecordMapper {
    List<OutpatientRecord> selectAll();
    List<OutpatientRecord> selectByCondition(OutpatientRecord record);
    OutpatientRecord selectById(Integer recordId);
    int insert(OutpatientRecord record);
    int update(OutpatientRecord record);
    int deleteById(Integer recordId);
} 