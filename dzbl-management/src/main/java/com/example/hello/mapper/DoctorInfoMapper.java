package com.example.hello.mapper;

import com.example.hello.entity.DoctorInfo;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DoctorInfoMapper {
    List<DoctorInfo> selectAll();
    List<DoctorInfo> selectByCondition(DoctorInfo doctor);
    DoctorInfo selectById(Integer doctorId);
    int insert(DoctorInfo doctor);
    int update(DoctorInfo doctor);
    int deleteById(Integer doctorId);
} 