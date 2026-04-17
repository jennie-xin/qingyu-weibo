package com.qingyu.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingyu.common.JwtUtil;
import com.qingyu.entity.User;
import com.qingyu.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> login(String username, String password) {
        User user = userMapper.selectOne(
            new QueryWrapper<User>().eq("username", username)
        );
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!hashPassword(password).equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", sanitizeUser(user));
        return result;
    }

    public User register(String username, String nickname, String password) {
        User existing = userMapper.selectOne(
            new QueryWrapper<User>().eq("username", username)
        );
        if (existing != null) {
            throw new RuntimeException("用户名已被占用");
        }
        User user = new User();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setPassword(hashPassword(password));
        user.setRole("user");
        user.setBio("");
        userMapper.insert(user);
        return sanitizeUser(user);
    }

    public User getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return sanitizeUser(user);
    }

    private User sanitizeUser(User user) {
        user.setPassword(null);
        return user;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("加密失败", e);
        }
    }
}
