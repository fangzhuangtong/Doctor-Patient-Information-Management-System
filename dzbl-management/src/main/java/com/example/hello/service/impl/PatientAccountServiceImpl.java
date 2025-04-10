package com.example.hello.service.impl;

import com.example.hello.entity.PatientAccount;
import com.example.hello.mapper.PatientAccountMapper;
import com.example.hello.service.PatientAccountService;
import com.example.hello.util.JwtUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PatientAccountServiceImpl implements PatientAccountService {
    
    @Autowired
    private PatientAccountMapper patientAccountMapper;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public List<PatientAccount> getAllAccounts() {
        return patientAccountMapper.selectAll();
    }
    
    @Override
    public PageInfo<PatientAccount> getAccountsByPage(String username, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        PatientAccount condition = new PatientAccount();
        condition.setUsername(username);
        List<PatientAccount> accounts = patientAccountMapper.selectByCondition(condition);
        return new PageInfo<>(accounts);
    }
    
    @Override
    public void deleteAccount(Integer userId) {
        patientAccountMapper.deleteById(userId);
    }
    
    @Override
    public PatientAccount register(PatientAccount account) {
        // 检查用户名是否已存在
        if (patientAccountMapper.selectByUsername(account.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 加密密码
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        
        // 插入数据
        patientAccountMapper.insert(account);
        return account;
    }
    
    @Override
    public String login(String username, String password) {
        PatientAccount account = patientAccountMapper.selectByUsername(username);
        if (account == null) {
            throw new RuntimeException("用户名不存在");
        }
        
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 生成JWT令牌
        return jwtUtil.generatePatientToken(account.getUserId(), account.getUsername());
    }
    
    @Override
    public void updatePassword(Integer userId, String oldPassword, String newPassword) {
        PatientAccount account = patientAccountMapper.selectById(userId);
        if (account == null) {
            throw new RuntimeException("账号不存在");
        }
        
        if (!passwordEncoder.matches(oldPassword, account.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        account.setPassword(passwordEncoder.encode(newPassword));
        patientAccountMapper.updatePassword(account);
    }
} 