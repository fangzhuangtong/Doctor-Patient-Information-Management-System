package com.example.hello.mapper;

import com.example.hello.entity.PatientAccount;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PatientAccountMapper {
    List<PatientAccount> selectAll();
    List<PatientAccount> selectByCondition(PatientAccount account);
    PatientAccount selectById(Integer userId);
    PatientAccount selectByUsername(String username);
    int insert(PatientAccount account);
    int updatePassword(PatientAccount account);
    int deleteById(Integer userId);
} 