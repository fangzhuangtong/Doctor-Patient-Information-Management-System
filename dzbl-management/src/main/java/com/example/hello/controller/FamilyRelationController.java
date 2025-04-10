package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.FamilyRelation;
import com.example.hello.service.FamilyRelationService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/family-relations")
public class FamilyRelationController {
    
    @Autowired
    private FamilyRelationService familyRelationService;
    
    @GetMapping
    public Result<List<FamilyRelation>> getAllRelations() {
        return Result.success(familyRelationService.getAllRelations());
    }
    
    @GetMapping(params = {"page", "pageSize"})
    public Result<PageInfo<FamilyRelation>> getRelationsByPage(
            @RequestParam(required = false) Integer patientId,
            @RequestParam(required = false) String relationType,
            @RequestParam Integer page,
            @RequestParam Integer pageSize) {
        return Result.success(familyRelationService.getRelationsByPage(patientId, relationType, page, pageSize));
    }
    
    @GetMapping("/{relationId}")
    public Result<FamilyRelation> getRelationById(@PathVariable Integer relationId) {
        return Result.success(familyRelationService.getRelationById(relationId));
    }
    
    @PostMapping
    public Result<?> addRelation(@RequestBody FamilyRelation relation) {
        // 添加参数校验
        if (relation.getPatientId() == null) {
            return Result.error("患者ID不能为空");
        }
        if (relation.getRelativeId() == null) {
            return Result.error("亲属ID不能为空");
        }
        if (relation.getRelationType() == null || relation.getRelationType().trim().isEmpty()) {
            return Result.error("亲属关系不能为空");
        }
        
        familyRelationService.addRelation(relation);
        return Result.success(null);
    }
    
    @PutMapping("/{relationId}")
    public Result<?> updateRelation(@PathVariable Integer relationId, @RequestBody FamilyRelation relation) {
        relation.setRelationId(relationId);
        familyRelationService.updateRelation(relation);
        return Result.success(null);
    }
    
    @DeleteMapping("/{relationId}")
    public Result<?> deleteRelation(@PathVariable Integer relationId) {
        familyRelationService.deleteRelation(relationId);
        return Result.success(null);
    }

    @GetMapping("/patient/{patientId}")
    public Result<List<FamilyRelation>> getRelationsByPatientId(@PathVariable Integer patientId) {
        if (patientId == null) {
            return Result.error("患者ID不能为空");
        }
        FamilyRelation condition = new FamilyRelation();
        condition.setPatientId(patientId);
        List<FamilyRelation> relations = familyRelationService.getRelationsByCondition(condition);
        return Result.success(relations);
    }

    @GetMapping("/relative/{relativeId}")
    public Result<List<FamilyRelation>> getRelationsByRelativeId(@PathVariable Integer relativeId) {
        if (relativeId == null) {
            return Result.error("亲属ID不能为空");
        }
        FamilyRelation condition = new FamilyRelation();
        condition.setRelativeId(relativeId);
        List<FamilyRelation> relations = familyRelationService.getRelationsByCondition(condition);
        return Result.success(relations);
    }
} 