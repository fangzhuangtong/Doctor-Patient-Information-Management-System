package com.example.hello.service;

import com.example.hello.entity.DoctorAccount;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface DoctorAccountService {
    List<DoctorAccount> getAllAccounts();
    PageInfo<DoctorAccount> getAccountsByPage(String username, Integer page, Integer pageSize);
    void deleteAccount(Integer doctorId);
    DoctorAccount register(DoctorAccount account);
    String login(String username, String password);
    void updatePassword(Integer doctorId, String oldPassword, String newPassword);
} 