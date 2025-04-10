package com.example.hello.mapper;

import com.example.hello.entity.Hospitalization;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface HospitalizationMapper {
    List<Hospitalization> selectAll();
    List<Hospitalization> selectByCondition(Hospitalization hospitalization);
    Hospitalization selectById(Integer hosId);
    int insert(Hospitalization hospitalization);
    int update(Hospitalization hospitalization);
    int deleteById(Integer hosId);
} 