package com.example.hello.service.impl;

import com.example.hello.entity.DoctorInfo;
import com.example.hello.mapper.DoctorInfoMapper;
import com.example.hello.service.DoctorInfoService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorInfoServiceImpl implements DoctorInfoService {
    
    @Autowired
    private DoctorInfoMapper doctorInfoMapper;
    
    @Override
    public List<DoctorInfo> getAllDoctors() {
        return doctorInfoMapper.selectAll();
    }
    
    @Override
    public PageInfo<DoctorInfo> getDoctorsByPage(Integer departmentId, String title, String introduction, String doctorName, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        DoctorInfo condition = new DoctorInfo();
        condition.setDepartmentId(departmentId);
        condition.setTitle(title);
        condition.setIntroduction(introduction);
        condition.setDoctorName(doctorName);
        List<DoctorInfo> doctors = doctorInfoMapper.selectByCondition(condition);
        return new PageInfo<>(doctors);
    }
    
    @Override
    public DoctorInfo getDoctorById(Integer doctorId) {
        return doctorInfoMapper.selectById(doctorId);
    }
    
    @Override
    public void addDoctor(DoctorInfo doctor) {
        doctorInfoMapper.insert(doctor);
    }
    
    @Override
    public void updateDoctor(DoctorInfo doctor) {
        doctorInfoMapper.update(doctor);
    }
    
    @Override
    public void deleteDoctor(Integer doctorId) {
        doctorInfoMapper.deleteById(doctorId);
    }
} 