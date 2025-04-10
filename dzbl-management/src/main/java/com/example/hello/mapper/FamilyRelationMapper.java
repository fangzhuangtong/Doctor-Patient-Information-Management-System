package com.example.hello.mapper;

import com.example.hello.entity.FamilyRelation;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface FamilyRelationMapper {
    List<FamilyRelation> selectAll();
    List<FamilyRelation> selectByCondition(FamilyRelation relation);
    FamilyRelation selectById(Integer relationId);
    int insert(FamilyRelation relation);
    int update(FamilyRelation relation);
    int deleteById(Integer relationId);
} 