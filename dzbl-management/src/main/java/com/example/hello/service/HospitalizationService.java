package com.example.hello.service;

import com.example.hello.entity.Hospitalization;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface HospitalizationService {
    List<Hospitalization> getAllHospitalizations();
    PageInfo<Hospitalization> getHospitalizationsByPage(Integer userId, Integer doctorId, String status, Integer page, Integer pageSize);
    Hospitalization getHospitalizationById(Integer hosId);
    void addHospitalization(Hospitalization hospitalization);
    void updateHospitalization(Hospitalization hospitalization);
    void deleteHospitalization(Integer hosId);
} 