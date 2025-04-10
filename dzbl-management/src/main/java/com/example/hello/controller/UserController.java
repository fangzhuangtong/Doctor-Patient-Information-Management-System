package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.User;
import com.example.hello.service.UserService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public Result<List<User>> getAllUsers() {
        return Result.success(userService.getAllUsers());
    }
    
    @GetMapping(params = {"page", "pageSize"})
    public Result<PageInfo<User>> getUsersByPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String gender,
            @RequestParam Integer page,
            @RequestParam Integer pageSize) {
        return Result.success(userService.getUsersByPage(name, gender, page, pageSize));
    }
    
    @GetMapping("/{userId}")
    public Result<User> getUserById(@PathVariable Integer userId) {
        return Result.success(userService.getUserById(userId));
    }
    
    @PostMapping
    public Result<?> addUser(@RequestBody User user) {
        // 添加参数校验
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return Result.error("姓名不能为空");
        }
        if (user.getBirthdate() == null) {
            return Result.error("出生日期不能为空");
        }
        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            return Result.error("联系方式不能为空");
        }
        
        userService.addUser(user);
        return Result.success(null);
    }
    
    @PutMapping("/{userId}")
    public Result<?> updateUser(@PathVariable Integer userId, @RequestBody User user) {
        user.setUserId(userId);
        userService.updateUser(user);
        return Result.success(null);
    }
    
    @DeleteMapping("/{userId}")
    public Result<?> deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
        return Result.success(null);
    }
} 