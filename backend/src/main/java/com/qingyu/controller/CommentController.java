package com.qingyu.controller;

import com.qingyu.common.Result;
import com.qingyu.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public Result<List<Map<String, Object>>> list(@PathVariable Long postId) {
        List<Map<String, Object>> data = commentService.getByPostId(postId);
        return Result.success(data);
    }

    @PostMapping
    public Result<Map<String, Object>> create(
            @PathVariable Long postId,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        String content = body.get("content");
        if (content == null || content.trim().isEmpty()) {
            return Result.error("评论内容不能为空");
        }
        try {
            Map<String, Object> data = commentService.create(postId, userId, content);
            return Result.success(data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
