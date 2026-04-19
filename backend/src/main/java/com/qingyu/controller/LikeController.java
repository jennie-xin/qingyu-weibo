package com.qingyu.controller;

import com.qingyu.common.Result;
import com.qingyu.service.LikeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts/{postId}/like")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping
    public Result<Map<String, Object>> toggle(@PathVariable Long postId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        try {
            Map<String, Object> data = likeService.toggle(postId, userId);
            return Result.success(data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
