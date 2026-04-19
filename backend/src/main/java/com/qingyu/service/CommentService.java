package com.qingyu.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingyu.entity.Comment;
import com.qingyu.entity.Post;
import com.qingyu.entity.User;
import com.qingyu.mapper.CommentMapper;
import com.qingyu.mapper.PostMapper;
import com.qingyu.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    public List<Map<String, Object>> getByPostId(Long postId) {
        QueryWrapper<Comment> query = new QueryWrapper<>();
        query.eq("post_id", postId).orderByDesc("created_at");
        List<Comment> comments = commentMapper.selectList(query);

        return comments.stream().map(comment -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", comment.getId());
            map.put("postId", comment.getPostId());
            map.put("userId", comment.getUserId());
            map.put("content", comment.getContent());
            map.put("createdAt", comment.getCreatedAt());

            User user = userMapper.selectById(comment.getUserId());
            if (user != null) {
                map.put("nickname", user.getNickname());
                map.put("avatar", user.getAvatar());
            }
            return map;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> create(Long postId, Long userId, String content) {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);
        commentMapper.insert(comment);

        Post post = postMapper.selectById(postId);
        if (post != null) {
            post.setCommentCount(post.getCommentCount() + 1);
            postMapper.updateById(post);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("id", comment.getId());
        map.put("postId", postId);
        map.put("userId", userId);
        map.put("content", content);
        map.put("createdAt", comment.getCreatedAt());

        User user = userMapper.selectById(userId);
        if (user != null) {
            map.put("nickname", user.getNickname());
            map.put("avatar", user.getAvatar());
        }
        return map;
    }
}
