package com.example.hello.controller;

import com.example.hello.common.Result;
import com.example.hello.entity.AdminAccount;
import com.example.hello.service.AdminAccountService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
public class AdminAccountController {
    
    @Autowired
    private AdminAccountService adminAccountService;
    
    // 管理员账号管理相关接口
    @RequestMapping("/admin-accounts")
    @RestController
    public class AdminManagementController {
        @GetMapping
        public Result<?> getAccounts(@RequestParam(required = false) String username,
                                    @RequestParam(required = false) Integer page,
                                    @RequestParam(required = false) Integer pageSize) {
            if (page != null && pageSize != null) {
                PageInfo<AdminAccount> pageInfo = adminAccountService.getAccountsByPage(username, page, pageSize);
                return Result.success(Map.of(
                    "total", pageInfo.getTotal(),
                    "rows", pageInfo.getList()
                ));
            } else {
                List<AdminAccount> accounts = adminAccountService.getAllAccounts();
                return Result.success(accounts);
            }
        }
        
        @DeleteMapping("/{adminId}")
        public Result<?> deleteAccount(@PathVariable Integer adminId) {
            adminAccountService.deleteAccount(adminId);
            return Result.success(null);
        }
        
        @PostMapping
        public Result<?> register(@RequestBody AdminAccount account) {
            if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            if (account.getPassword() == null || account.getPassword().trim().isEmpty()) {
                return Result.error("密码不能为空");
            }
            
            AdminAccount registered = adminAccountService.register(account);
            return Result.success(Map.of("adminId", registered.getAdminId()));
        }
        
        @PutMapping("/{adminId}/password")
        public Result<?> updatePassword(@PathVariable Integer adminId,
                                      @RequestBody Map<String, String> params) {
            String oldPassword = params.get("oldPassword");
            String newPassword = params.get("newPassword");
            
            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                return Result.error("原密码不能为空");
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return Result.error("新密码不能为空");
            }
            
            adminAccountService.updatePassword(adminId, oldPassword, newPassword);
            return Result.success("密码修改成功");
        }
    }
    
    // 管理员登录接口
    @PostMapping("/admin/login")
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
            String token = adminAccountService.login(username, password);
            Map<String, String> data = new HashMap<>();
            data.put("token", token);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
} 