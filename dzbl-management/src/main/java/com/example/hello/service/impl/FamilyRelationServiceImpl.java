package com.example.hello.service.impl;

import com.example.hello.entity.FamilyRelation;
import com.example.hello.mapper.FamilyRelationMapper;
import com.example.hello.service.FamilyRelationService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FamilyRelationServiceImpl implements FamilyRelationService {
    
    @Autowired
    private FamilyRelationMapper familyRelationMapper;
    
    @Override
    public List<FamilyRelation> getAllRelations() {
        return familyRelationMapper.selectAll();
    }
    
    @Override
    public PageInfo<FamilyRelation> getRelationsByPage(Integer patientId, String relationType, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        FamilyRelation condition = new FamilyRelation();
        condition.setPatientId(patientId);
        condition.setRelationType(relationType);
        List<FamilyRelation> relations = familyRelationMapper.selectByCondition(condition);
        return new PageInfo<>(relations);
    }
    
    @Override
    public FamilyRelation getRelationById(Integer relationId) {
        return familyRelationMapper.selectById(relationId);
    }
    
    @Override
    public void addRelation(FamilyRelation relation) {
        // 设置默认值
        if (relation.getIsValid() == null) {
            relation.setIsValid(true);
        }
        familyRelationMapper.insert(relation);
    }
    
    @Override
    public void updateRelation(FamilyRelation relation) {
        familyRelationMapper.update(relation);
    }
    
    @Override
    public void deleteRelation(Integer relationId) {
        familyRelationMapper.deleteById(relationId);
    }
    
    @Override
    public List<FamilyRelation> getRelationsByCondition(FamilyRelation relation) {
        return familyRelationMapper.selectByCondition(relation);
    }
} 