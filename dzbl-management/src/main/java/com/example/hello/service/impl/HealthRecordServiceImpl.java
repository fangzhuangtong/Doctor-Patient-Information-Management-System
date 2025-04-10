package com.example.hello.service.impl;

import com.example.hello.entity.HealthRecord;
import com.example.hello.mapper.HealthRecordMapper;
import com.example.hello.service.HealthRecordService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.example.hello.config.RiskScoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.hello.util.RiskScoreCalculator;

import java.math.BigDecimal;
import java.util.List;

@Service
public class HealthRecordServiceImpl implements HealthRecordService {
    
    @Autowired
    private HealthRecordMapper healthRecordMapper;
    
    @Autowired
    private RiskScoreCalculator riskScoreCalculator;
    
    @Override
    public List<HealthRecord> getAllRecords() {
        return healthRecordMapper.selectAll();
    }
    
    @Override
    public PageInfo<HealthRecord> getRecordsByPage(Integer userId, String bloodType, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        HealthRecord condition = new HealthRecord();
        condition.setUserId(userId);
        condition.setBloodType(bloodType);
        List<HealthRecord> records = healthRecordMapper.selectByCondition(condition);
        return new PageInfo<>(records);
    }
    
    @Override
    public HealthRecord getRecordById(Integer recordId) {
        return healthRecordMapper.selectById(recordId);
    }
    
    @Override
    public void addRecord(HealthRecord record) {
        // 计算BMI
        if (record.getHeight() != null && record.getWeight() != null) {
            record.setBmi(riskScoreCalculator.calculateBMI(record.getHeight(), record.getWeight()));
        }
        
        // 计算风险分数
        record.setRiskScore(riskScoreCalculator.calculateRiskScore(record));
        
        healthRecordMapper.insert(record);
    }
    
    @Override
    public void updateRecord(HealthRecord record) {
        // 如果更新了身高或体重，重新计算BMI
        if (record.getHeight() != null && record.getWeight() != null) {
            record.setBmi(riskScoreCalculator.calculateBMI(record.getHeight(), record.getWeight()));
        }
        
        // 重新计算风险分数
        record.setRiskScore(riskScoreCalculator.calculateRiskScore(record));
        
        healthRecordMapper.update(record);
    }
    
    @Override
    public void deleteRecord(Integer recordId) {
        healthRecordMapper.deleteById(recordId);
    }
    
    @Override
    public List<HealthRecord> getRecordsByUserId(Integer userId) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        return healthRecordMapper.selectByUserId(userId);
    }
} 