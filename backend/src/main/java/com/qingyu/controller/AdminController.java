package com.qingyu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingyu.common.Result;
import com.qingyu.entity.Post;
import com.qingyu.entity.User;
import com.qingyu.mapper.PostMapper;
import com.qingyu.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostMapper postMapper;

    private boolean checkAdmin(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        return "admin".equals(role);
    }

    @GetMapping("/users")
    public Result<List<User>> getUsers(HttpServletRequest request) {
        if (!checkAdmin(request)) {
            return Result.error(403, "无权限");
        }
        List<User> users = userMapper.selectList(
            new QueryWrapper<User>().orderByDesc("created_at")
        );
        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }

    @PutMapping("/users/{id}/role")
    public Result<Void> updateRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        if (!checkAdmin(request)) {
            return Result.error(403, "无权限");
        }
        String role = body.get("role");
        if (!"admin".equals(role) && !"user".equals(role)) {
            return Result.error("角色值无效");
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setRole(role);
        userMapper.updateById(user);
        return Result.success();
    }

    @PutMapping("/users/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        if (!checkAdmin(request)) {
            return Result.error(403, "无权限");
        }
        String status = body.get("status");
        if (!"active".equals(status) && !"banned".equals(status)) {
            return Result.error("状态值无效");
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/posts/{id}")
    public Result<Void> deletePost(@PathVariable Long id, HttpServletRequest request) {
        if (!checkAdmin(request)) {
            return Result.error(403, "无权限");
        }
        Post post = postMapper.selectById(id);
        if (post == null) {
            return Result.error("微博不存在");
        }
        postMapper.deleteById(id);
        return Result.success();
    }
}
