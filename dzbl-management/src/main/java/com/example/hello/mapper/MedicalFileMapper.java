package com.example.hello.mapper;

import com.example.hello.entity.MedicalFile;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MedicalFileMapper {
    List<MedicalFile> selectAll();
    List<MedicalFile> selectByCondition(MedicalFile file);
    MedicalFile selectById(Integer fileId);
    int insert(MedicalFile file);
    int update(MedicalFile file);
    int deleteById(Integer fileId);
} 