package com.example.hello.service;

import com.example.hello.entity.FamilyRelation;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface FamilyRelationService {
    List<FamilyRelation> getAllRelations();
    PageInfo<FamilyRelation> getRelationsByPage(Integer patientId, String relationType, Integer page, Integer pageSize);
    FamilyRelation getRelationById(Integer relationId);
    void addRelation(FamilyRelation relation);
    void updateRelation(FamilyRelation relation);
    void deleteRelation(Integer relationId);
    List<FamilyRelation> getRelationsByCondition(FamilyRelation relation);
} 