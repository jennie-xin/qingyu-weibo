package com.qingyu.controller;

import com.qingyu.common.Result;
import com.qingyu.entity.User;
import com.qingyu.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }
        try {
            Map<String, Object> data = authService.login(username, password);
            return Result.success(data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String nickname = body.get("nickname");
        String password = body.get("password");
        if (username == null || nickname == null || password == null) {
            return Result.error("请填写完整信息");
        }
        if (username.length() < 4 || username.length() > 16) {
            return Result.error("用户名长度4-16位");
        }
        if (password.length() < 6) {
            return Result.error("密码至少6位");
        }
        try {
            User user = authService.register(username, nickname, password);
            return Result.success(user);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public Result<User> profile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        try {
            User user = authService.getProfile(userId);
            return Result.success(user);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
