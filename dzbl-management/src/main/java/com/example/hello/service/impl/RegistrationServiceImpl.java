package com.example.hello.service.impl;

import com.example.hello.entity.Registration;
import com.example.hello.mapper.RegistrationMapper;
import com.example.hello.service.RegistrationService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    
    @Autowired
    private RegistrationMapper registrationMapper;
    
    @Override
    public List<Registration> getAllRegistrations() {
        return registrationMapper.selectAll();
    }
    
    @Override
    public PageInfo<Registration> getRegistrationsByPage(Integer userId, Integer doctorId, String status, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Registration condition = new Registration();
        condition.setUserId(userId);
        condition.setDoctorId(doctorId);
        condition.setStatus(status);
        List<Registration> list = registrationMapper.selectByCondition(condition);
        return new PageInfo<>(list);
    }
    
    @Override
    public Registration getRegistrationById(Integer regId) {
        return registrationMapper.selectById(regId);
    }
    
    @Override
    public void addRegistration(Registration registration) {
        // 设置默认值
        if (registration.getStatus() == null) {
            registration.setStatus("待就诊");
        }
        registrationMapper.insert(registration);
    }
    
    @Override
    public void updateRegistration(Registration registration) {
        registrationMapper.update(registration);
    }
    
    @Override
    public void deleteRegistration(Integer regId) {
        registrationMapper.deleteById(regId);
    }
} 