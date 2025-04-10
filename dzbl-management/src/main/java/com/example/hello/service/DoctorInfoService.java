package com.example.hello.service;

import com.example.hello.entity.DoctorInfo;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface DoctorInfoService {
    List<DoctorInfo> getAllDoctors();
    PageInfo<DoctorInfo> getDoctorsByPage(Integer departmentId, String title, String introduction, String doctorName, Integer page, Integer pageSize);
    DoctorInfo getDoctorById(Integer doctorId);
    void addDoctor(DoctorInfo doctor);
    void updateDoctor(DoctorInfo doctor);
    void deleteDoctor(Integer doctorId);
} 