package com.example.hello.service;

import com.example.hello.entity.User;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    PageInfo<User> getUsersByPage(String name, String gender, Integer page, Integer pageSize);
    User getUserById(Integer userId);
    void addUser(User user);
    void updateUser(User user);
    void deleteUser(Integer userId);
} 