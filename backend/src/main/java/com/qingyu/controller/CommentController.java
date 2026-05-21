package com.qingyu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingyu.common.Result;
import com.qingyu.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
            @RequestParam("content") String content,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        if (content == null || content.trim().isEmpty()) {
            return Result.error("评论内容不能为空");
        }
        try {
            String imagesJson = null;
            if (images != null && images.length > 0) {
                String dirPath = System.getProperty("user.dir") + "/uploads";
                File dir = new File(dirPath);
                if (!dir.exists()) dir.mkdirs();

                List<String> urls = new ArrayList<>();
                for (MultipartFile file : images) {
                    if (file.isEmpty()) continue;
                    String originalName = file.getOriginalFilename();
                    String ext = "";
                    if (originalName != null && originalName.contains(".")) {
                        ext = originalName.substring(originalName.lastIndexOf("."));
                    }
                    String filename = UUID.randomUUID().toString().replace("-", "") + ext;
                    File dest = new File(dir, filename);
                    file.transferTo(dest);
                    urls.add("/uploads/" + filename);
                }
                if (!urls.isEmpty()) {
                    imagesJson = new ObjectMapper().writeValueAsString(urls);
                }
            }
            Map<String, Object> data = commentService.create(postId, userId, content, imagesJson);
            return Result.success(data);
        } catch (IOException e) {
            return Result.error("图片上传失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
