package com.example.hello.service;

import com.example.hello.entity.Consultation;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface ConsultationService {
    List<Consultation> getAllConsultations();
    PageInfo<Consultation> getConsultationsByPage(Integer userId, Integer doctorId, String status, Integer page, Integer pageSize);
    Consultation getConsultationById(Integer consultId);
    void addConsultation(Consultation consultation);
    void updateConsultation(Consultation consultation);
    void deleteConsultation(Integer consultId);
    List<Consultation> getConsultationsByCondition(Consultation consultation);
} 