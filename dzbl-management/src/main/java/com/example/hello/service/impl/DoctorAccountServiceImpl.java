package com.example.hello.service.impl;

import com.example.hello.entity.DoctorAccount;
import com.example.hello.mapper.DoctorAccountMapper;
import com.example.hello.service.DoctorAccountService;
import com.example.hello.util.JwtUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DoctorAccountServiceImpl implements DoctorAccountService {
    
    @Autowired
    private DoctorAccountMapper doctorAccountMapper;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public List<DoctorAccount> getAllAccounts() {
        return doctorAccountMapper.selectAll();
    }
    
    @Override
    public PageInfo<DoctorAccount> getAccountsByPage(String username, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        DoctorAccount condition = new DoctorAccount();
        condition.setUsername(username);
        List<DoctorAccount> accounts = doctorAccountMapper.selectByCondition(condition);
        return new PageInfo<>(accounts);
    }
    
    @Override
    public void deleteAccount(Integer doctorId) {
        doctorAccountMapper.deleteById(doctorId);
    }
    
    @Override
    public DoctorAccount register(DoctorAccount account) {
        // 检查用户名是否已存在
        if (doctorAccountMapper.selectByUsername(account.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 加密密码
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        
        // 插入数据
        doctorAccountMapper.insert(account);
        return account;
    }
    
    @Override
    public String login(String username, String password) {
        DoctorAccount account = doctorAccountMapper.selectByUsername(username);
        if (account == null) {
            throw new RuntimeException("用户名不存在");
        }
        
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 生成JWT令牌
        return jwtUtil.generateToken(account.getDoctorId(), account.getUsername());
    }
    
    @Override
    public void updatePassword(Integer doctorId, String oldPassword, String newPassword) {
        DoctorAccount account = doctorAccountMapper.selectById(doctorId);
        if (account == null) {
            throw new RuntimeException("账号不存在");
        }
        
        if (!passwordEncoder.matches(oldPassword, account.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        account.setPassword(passwordEncoder.encode(newPassword));
        doctorAccountMapper.updatePassword(account);
    }
} 