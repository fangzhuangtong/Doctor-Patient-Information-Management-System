package com.example.hello.service;

import com.example.hello.entity.AdminAccount;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface AdminAccountService {
    List<AdminAccount> getAllAccounts();
    PageInfo<AdminAccount> getAccountsByPage(String username, Integer page, Integer pageSize);
    void deleteAccount(Integer adminId);
    AdminAccount register(AdminAccount account);
    String login(String username, String password);
    void updatePassword(Integer adminId, String oldPassword, String newPassword);
} 