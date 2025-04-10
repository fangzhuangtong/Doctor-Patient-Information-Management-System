package com.example.hello.service;

import com.example.hello.entity.Registration;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface RegistrationService {
    List<Registration> getAllRegistrations();
    PageInfo<Registration> getRegistrationsByPage(Integer userId, Integer doctorId, String status, Integer page, Integer pageSize);
    Registration getRegistrationById(Integer regId);
    void addRegistration(Registration registration);
    void updateRegistration(Registration registration);
    void deleteRegistration(Integer regId);
} 