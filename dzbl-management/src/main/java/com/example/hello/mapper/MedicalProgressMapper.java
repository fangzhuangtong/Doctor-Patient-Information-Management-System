package com.example.hello.mapper;

import com.example.hello.entity.MedicalProgress;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MedicalProgressMapper {
    List<MedicalProgress> selectAll();
    List<MedicalProgress> selectByCondition(MedicalProgress progress);
    MedicalProgress selectById(Integer reportId);
    int insert(MedicalProgress progress);
    int update(MedicalProgress progress);
    int deleteById(Integer reportId);
} 