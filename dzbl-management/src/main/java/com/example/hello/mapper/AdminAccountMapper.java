package com.example.hello.mapper;

import com.example.hello.entity.AdminAccount;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AdminAccountMapper {
    List<AdminAccount> selectAll();
    List<AdminAccount> selectByCondition(AdminAccount account);
    AdminAccount selectById(Integer adminId);
    AdminAccount selectByUsername(String username);
    int insert(AdminAccount account);
    int updatePassword(AdminAccount account);
    int deleteById(Integer adminId);
} 