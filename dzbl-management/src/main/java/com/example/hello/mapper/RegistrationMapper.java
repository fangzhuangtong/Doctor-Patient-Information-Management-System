package com.example.hello.mapper;

import com.example.hello.entity.Registration;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface RegistrationMapper {
    List<Registration> selectAll();
    List<Registration> selectByCondition(Registration registration);
    Registration selectById(Integer regId);
    int insert(Registration registration);
    int update(Registration registration);
    int deleteById(Integer regId);
} 