package com.qingyu.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingyu.entity.Like;
import com.qingyu.entity.Post;
import com.qingyu.mapper.LikeMapper;
import com.qingyu.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LikeService {

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private NotificationService notificationService;

    public Map<String, Object> toggle(Long postId, Long userId) {
        QueryWrapper<Like> query = new QueryWrapper<>();
        query.eq("post_id", postId).eq("user_id", userId);
        Like existing = likeMapper.selectOne(query);

        Map<String, Object> result = new HashMap<>();
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("微博不存在");
        }

        if (existing != null) {
            likeMapper.deleteById(existing.getId());
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            postMapper.updateById(post);
            result.put("liked", false);
        } else {
            Like like = new Like();
            like.setPostId(postId);
            like.setUserId(userId);
            likeMapper.insert(like);
            post.setLikeCount(post.getLikeCount() + 1);
            postMapper.updateById(post);
            result.put("liked", true);
            notificationService.createNotification(post.getUserId(), userId, "like", postId);
        }

        result.put("likeCount", post.getLikeCount());
        return result;
    }
}
