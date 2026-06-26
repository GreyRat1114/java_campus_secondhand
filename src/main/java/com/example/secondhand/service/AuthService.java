package com.example.secondhand.service;

import com.example.secondhand.entity.User;
import com.example.secondhand.mapper.UserMapper;
import com.example.secondhand.util.HashUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthService {
    private final UserMapper userMapper;

    public AuthService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) return null;
        if (!"NORMAL".equals(user.getStatus())) return null;
        if (!HashUtil.sha256(password).equals(user.getPassword())) return null;
        return user;
    }

    public String register(String username, String password, String phone) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return "用户名和密码不能为空";
        }
        if (password.length() < 6) {
            return "密码至少 6 位";
        }
        if (userMapper.findByUsername(username) != null) {
            return "用户名已存在";
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(HashUtil.sha256(password));
        user.setPhone(phone);
        user.setRole("USER");
        user.setCreditScore(100);
        user.setStatus("NORMAL");
        userMapper.insert(user);
        return null;
    }
}
