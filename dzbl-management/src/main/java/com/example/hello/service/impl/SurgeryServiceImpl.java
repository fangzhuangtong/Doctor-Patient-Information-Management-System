package com.example.hello.service.impl;

import com.example.hello.entity.Surgery;
import com.example.hello.mapper.SurgeryMapper;
import com.example.hello.service.SurgeryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SurgeryServiceImpl implements SurgeryService {
    
    @Autowired
    private SurgeryMapper surgeryMapper;
    
    @Override
    public List<Surgery> getAllSurgeries() {
        return surgeryMapper.selectAll();
    }
    
    @Override
    public PageInfo<Surgery> getSurgeriesByPage(Integer userId, String status, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Surgery condition = new Surgery();
        condition.setUserId(userId);
        condition.setStatus(status);
        List<Surgery> surgeries = surgeryMapper.selectByCondition(condition);
        return new PageInfo<>(surgeries);
    }
    
    @Override
    public Surgery getSurgeryById(Integer surgeryId) {
        return surgeryMapper.selectById(surgeryId);
    }
    
    @Override
    public void addSurgery(Surgery surgery) {
        // 设置默认值
        if (surgery.getStatus() == null) {
            surgery.setStatus("未进行");
        }
        surgeryMapper.insert(surgery);
    }
    
    @Override
    public void updateSurgery(Surgery surgery) {
        surgeryMapper.update(surgery);
    }
    
    @Override
    public void deleteSurgery(Integer surgeryId) {
        surgeryMapper.deleteById(surgeryId);
    }
} 