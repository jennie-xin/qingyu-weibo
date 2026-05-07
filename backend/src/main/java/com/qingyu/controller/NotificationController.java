package com.qingyu.controller;

import com.qingyu.common.Result;
import com.qingyu.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public Result<List<Map<String, Object>>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        List<Map<String, Object>> data = notificationService.getByUserId(userId);
        return Result.success(data);
    }

    @PutMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        notificationService.markRead(id, userId);
        return Result.success();
    }

    @PutMapping("/read-all")
    public Result<Void> markAllRead(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        notificationService.markAllRead(userId);
        return Result.success();
    }
}
