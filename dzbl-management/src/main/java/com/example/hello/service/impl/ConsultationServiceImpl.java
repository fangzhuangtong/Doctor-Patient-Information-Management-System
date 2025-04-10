package com.example.hello.service.impl;

import com.example.hello.entity.Consultation;
import com.example.hello.mapper.ConsultationMapper;
import com.example.hello.service.ConsultationService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultationServiceImpl implements ConsultationService {
    
    @Autowired
    private ConsultationMapper consultationMapper;
    
    @Override
    public List<Consultation> getAllConsultations() {
        return consultationMapper.selectAll();
    }
    
    @Override
    public PageInfo<Consultation> getConsultationsByPage(Integer userId, Integer doctorId, String status, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Consultation condition = new Consultation();
        condition.setUserId(userId);
        condition.setDoctorId(doctorId);
        condition.setStatus(status);
        List<Consultation> consultations = consultationMapper.selectByCondition(condition);
        return new PageInfo<>(consultations);
    }
    
    @Override
    public Consultation getConsultationById(Integer consultId) {
        return consultationMapper.selectById(consultId);
    }
    
    @Override
    public void addConsultation(Consultation consultation) {
        // 设置默认值
        if (consultation.getConsultType() == null) {
            consultation.setConsultType("文字");
        }
        if (consultation.getStatus() == null) {
            consultation.setStatus("未回复");
        }
        consultationMapper.insert(consultation);
    }
    
    @Override
    public void updateConsultation(Consultation consultation) {
        consultationMapper.update(consultation);
    }
    
    @Override
    public void deleteConsultation(Integer consultId) {
        consultationMapper.deleteById(consultId);
    }
    
    @Override
    public List<Consultation> getConsultationsByCondition(Consultation consultation) {
        return consultationMapper.selectByCondition(consultation);
    }
} 