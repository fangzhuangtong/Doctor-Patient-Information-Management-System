package com.example.hello.service.impl;

import com.example.hello.entity.User;
import com.example.hello.mapper.UserMapper;
import com.example.hello.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }
    
    @Override
    public PageInfo<User> getUsersByPage(String name, String gender, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        User condition = new User();
        condition.setName(name);
        condition.setGender(gender);
        List<User> users = userMapper.selectByCondition(condition);
        return new PageInfo<>(users);
    }
    
    @Override
    public User getUserById(Integer userId) {
        return userMapper.selectById(userId);
    }
    
    @Override
    public void addUser(User user) {
        userMapper.insert(user);
    }
    
    @Override
    public void updateUser(User user) {
        userMapper.update(user);
    }
    
    @Override
    public void deleteUser(Integer userId) {
        userMapper.deleteById(userId);
    }
} 