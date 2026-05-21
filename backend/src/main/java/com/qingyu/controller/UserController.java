package com.qingyu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingyu.common.Result;
import com.qingyu.entity.Post;
import com.qingyu.entity.User;
import com.qingyu.mapper.LikeMapper;
import com.qingyu.mapper.PostMapper;
import com.qingyu.mapper.UserMapper;
import com.qingyu.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private PostService postService;

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getInfo(@PathVariable Long id, HttpServletRequest request) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);

        Long currentUserId = (Long) request.getAttribute("userId");

        long postCount = postMapper.selectCount(
            new QueryWrapper<Post>().eq("user_id", id)
        );

        List<Post> userPosts = postMapper.selectList(
            new QueryWrapper<Post>().eq("user_id", id)
        );
        int totalLikes = userPosts.stream()
            .mapToInt(Post::getLikeCount)
            .sum();

        List<Map<String, Object>> posts = postService.getByUserId(id, currentUserId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("postCount", postCount);
        stats.put("likeCount", totalLikes);

        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("posts", posts);
        data.put("stats", stats);

        return Result.success(data);
    }

    @GetMapping("/{id}/liked")
    public Result<List<Map<String, Object>>> getLikedPosts(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        List<Map<String, Object>> posts = postService.getLikedByUserId(id, currentUserId);
        return Result.success(posts);
    }

    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestBody Map<String, String> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (body.containsKey("nickname")) {
            user.setNickname(body.get("nickname"));
        }
        if (body.containsKey("bio")) {
            user.setBio(body.get("bio"));
        }
        if (body.containsKey("avatar")) {
            user.setAvatar(body.get("avatar"));
        }
        if (body.containsKey("cover")) {
            user.setCover(body.get("cover"));
        }

        userMapper.updateById(user);
        user.setPassword(null);
        return Result.success(user);
    }
}
