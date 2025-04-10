package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.PatientAccount;
import com.example.hello.service.PatientAccountService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/patient-accounts")
public class PatientAccountController {
    
    @Autowired
    private PatientAccountService patientAccountService;
    
    @GetMapping
    public Result<?> getAccounts(@RequestParam(required = false) String username,
                                @RequestParam(required = false) Integer page,
                                @RequestParam(required = false) Integer pageSize) {
        if (page != null && pageSize != null) {
            PageInfo<PatientAccount> pageInfo = patientAccountService.getAccountsByPage(username, page, pageSize);
            return Result.success(Map.of(
                "total", pageInfo.getTotal(),
                "rows", pageInfo.getList()
            ));
        } else {
            List<PatientAccount> accounts = patientAccountService.getAllAccounts();
            return Result.success(accounts);
        }
    }
    
    @DeleteMapping("/{userId}")
    public Result<?> deleteAccount(@PathVariable Integer userId) {
        patientAccountService.deleteAccount(userId);
        return Result.success(null);
    }
    
    @PostMapping
    public Result<?> register(@RequestBody PatientAccount account) {
        if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (account.getPassword() == null || account.getPassword().trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        
        PatientAccount registered = patientAccountService.register(account);
        return Result.success(Map.of("userId", registered.getUserId()));
    }
    
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");
        
        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        
        try {
            String token = patientAccountService.login(username, password);
            Map<String, String> data = new HashMap<>();
            data.put("token", token);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/{userId}/password")
    public Result<?> updatePassword(@PathVariable Integer userId,
                                  @RequestBody Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            return Result.error("原密码不能为空");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return Result.error("新密码不能为空");
        }
        
        patientAccountService.updatePassword(userId, oldPassword, newPassword);
        return Result.success("密码修改成功");
    }
} 