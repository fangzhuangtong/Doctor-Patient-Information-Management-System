package com.example.hello.service.impl;

import com.example.hello.entity.Hospitalization;
import com.example.hello.mapper.HospitalizationMapper;
import com.example.hello.service.HospitalizationService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalizationServiceImpl implements HospitalizationService {
    
    @Autowired
    private HospitalizationMapper hospitalizationMapper;
    
    @Override
    public List<Hospitalization> getAllHospitalizations() {
        return hospitalizationMapper.selectAll();
    }
    
    @Override
    public PageInfo<Hospitalization> getHospitalizationsByPage(Integer userId, Integer doctorId, String status, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Hospitalization condition = new Hospitalization();
        condition.setUserId(userId);
        condition.setDoctorId(doctorId);
        condition.setStatus(status);
        List<Hospitalization> hospitalizations = hospitalizationMapper.selectByCondition(condition);
        return new PageInfo<>(hospitalizations);
    }
    
    @Override
    public Hospitalization getHospitalizationById(Integer hosId) {
        return hospitalizationMapper.selectById(hosId);
    }
    
    @Override
    public void addHospitalization(Hospitalization hospitalization) {
        hospitalizationMapper.insert(hospitalization);
    }
    
    @Override
    public void updateHospitalization(Hospitalization hospitalization) {
        hospitalizationMapper.update(hospitalization);
    }
    
    @Override
    public void deleteHospitalization(Integer hosId) {
        hospitalizationMapper.deleteById(hosId);
    }
} 