package com.qingyu.controller;

import com.qingyu.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${spring.servlet.multipart.location:uploads}")
    private String uploadDir;

    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
        String dirPath = System.getProperty("user.dir") + "/uploads";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            File dest = new File(dir, fileName);
            file.transferTo(dest);
            String url = "/uploads/" + fileName;
            return Result.success(url);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
