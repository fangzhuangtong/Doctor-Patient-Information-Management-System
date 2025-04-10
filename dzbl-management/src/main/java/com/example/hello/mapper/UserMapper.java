package com.example.hello.mapper;

import com.example.hello.entity.User;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserMapper {
    List<User> selectAll();
    List<User> selectByCondition(User user);
    User selectById(Integer userId);
    int insert(User user);
    int update(User user);
    int deleteById(Integer userId);
} 