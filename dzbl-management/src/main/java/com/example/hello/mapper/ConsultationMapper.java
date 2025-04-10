package com.example.hello.mapper;

import com.example.hello.entity.Consultation;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ConsultationMapper {
    List<Consultation> selectAll();
    List<Consultation> selectByCondition(Consultation consultation);
    Consultation selectById(Integer consultId);
    int insert(Consultation consultation);
    int update(Consultation consultation);
    int deleteById(Integer consultId);
} 