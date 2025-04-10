package com.example.hello.service;

import com.example.hello.entity.Surgery;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface SurgeryService {
    List<Surgery> getAllSurgeries();
    PageInfo<Surgery> getSurgeriesByPage(Integer userId, String status, Integer page, Integer pageSize);
    Surgery getSurgeryById(Integer surgeryId);
    void addSurgery(Surgery surgery);
    void updateSurgery(Surgery surgery);
    void deleteSurgery(Integer surgeryId);
} 