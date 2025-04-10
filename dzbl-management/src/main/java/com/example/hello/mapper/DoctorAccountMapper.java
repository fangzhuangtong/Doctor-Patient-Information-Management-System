package com.example.hello.mapper;

import com.example.hello.entity.DoctorAccount;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DoctorAccountMapper {
    List<DoctorAccount> selectAll();
    List<DoctorAccount> selectByCondition(DoctorAccount account);
    DoctorAccount selectById(Integer doctorId);
    DoctorAccount selectByUsername(String username);
    int insert(DoctorAccount account);
    int updatePassword(DoctorAccount account);
    int deleteById(Integer doctorId);
} 