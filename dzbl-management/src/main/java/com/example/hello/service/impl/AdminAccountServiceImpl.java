package com.example.hello.service.impl;

import com.example.hello.entity.AdminAccount;
import com.example.hello.mapper.AdminAccountMapper;
import com.example.hello.service.AdminAccountService;
import com.example.hello.util.JwtUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AdminAccountServiceImpl implements AdminAccountService {
    
    @Autowired
    private AdminAccountMapper adminAccountMapper;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public List<AdminAccount> getAllAccounts() {
        return adminAccountMapper.selectAll();
    }
    
    @Override
    public PageInfo<AdminAccount> getAccountsByPage(String username, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        AdminAccount condition = new AdminAccount();
        condition.setUsername(username);
        List<AdminAccount> accounts = adminAccountMapper.selectByCondition(condition);
        return new PageInfo<>(accounts);
    }
    
    @Override
    public void deleteAccount(Integer adminId) {
        adminAccountMapper.deleteById(adminId);
    }
    
    @Override
    public AdminAccount register(AdminAccount account) {
        // 检查用户名是否已存在
        if (adminAccountMapper.selectByUsername(account.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 加密密码
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        
        // 插入数据
        adminAccountMapper.insert(account);
        return account;
    }
    
    @Override
    public String login(String username, String password) {
        AdminAccount account = adminAccountMapper.selectByUsername(username);
        if (account == null) {
            throw new RuntimeException("用户名不存在");
        }
        
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 生成JWT令牌
        return jwtUtil.generateAdminToken(account.getAdminId(), account.getUsername());
    }
    
    @Override
    public void updatePassword(Integer adminId, String oldPassword, String newPassword) {
        AdminAccount account = adminAccountMapper.selectById(adminId);
        if (account == null) {
            throw new RuntimeException("账号不存在");
        }
        
        if (!passwordEncoder.matches(oldPassword, account.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        account.setPassword(passwordEncoder.encode(newPassword));
        adminAccountMapper.updatePassword(account);
    }
} 