package com.example.hello.mapper;

import com.example.hello.entity.Surgery;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface SurgeryMapper {
    List<Surgery> selectAll();
    List<Surgery> selectByCondition(Surgery surgery);
    Surgery selectById(Integer surgeryId);
    int insert(Surgery surgery);
    int update(Surgery surgery);
    int deleteById(Integer surgeryId);
} 