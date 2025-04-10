package com.example.hello.service;

import com.example.hello.entity.PatientAccount;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface PatientAccountService {
    List<PatientAccount> getAllAccounts();
    PageInfo<PatientAccount> getAccountsByPage(String username, Integer page, Integer pageSize);
    void deleteAccount(Integer userId);
    PatientAccount register(PatientAccount account);
    String login(String username, String password);
    void updatePassword(Integer userId, String oldPassword, String newPassword);
} 