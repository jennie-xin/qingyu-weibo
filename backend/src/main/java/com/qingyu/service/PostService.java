package com.qingyu.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingyu.entity.Post;
import com.qingyu.entity.User;
import com.qingyu.mapper.LikeMapper;
import com.qingyu.mapper.PostMapper;
import com.qingyu.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private TopicService topicService;

    public Map<String, Object> getList(int page, int size, Long currentUserId) {
        Page<Post> pageParam = new Page<>(page, size);
        QueryWrapper<Post> query = new QueryWrapper<>();
        query.orderByDesc("created_at");

        IPage<Post> result = postMapper.selectPage(pageParam, query);
        List<Map<String, Object>> records = result.getRecords().stream()
                .map(post -> enrichPost(post, currentUserId))
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("records", records);
        data.put("total", result.getTotal());
        data.put("pages", result.getPages());
        return data;
    }

    public Map<String, Object> getDetail(Long postId, Long currentUserId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("微博不存在");
        }
        postMapper.incrementViewCount(postId);
        post.setViewCount(post.getViewCount() == null ? 1 : post.getViewCount() + 1);
        return enrichPost(post, currentUserId);
    }

    public Map<String, Object> create(Long userId, String content, String images) {
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setImages(images);
        post.setLikeCount(0);
        post.setCommentCount(0);
        postMapper.insert(post);
        topicService.extractAndSaveTopics(content, post.getId());
        return enrichPost(post, userId);
    }

    public void delete(Long postId, Long userId, String role) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("微博不存在");
        }
        if (!post.getUserId().equals(userId) && !"admin".equals(role)) {
            throw new RuntimeException("没有权限删除");
        }
        topicService.decrementTopics(post.getContent());
        postMapper.deleteById(postId);
    }

    public Map<String, Object> update(Long postId, Long userId, String role, String content, String images) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("微博不存在");
        }
        if (!post.getUserId().equals(userId) && !"admin".equals(role)) {
            throw new RuntimeException("没有权限编辑");
        }
        post.setContent(content);
        post.setImages(images);
        postMapper.updateById(post);
        topicService.extractAndSaveTopics(content, post.getId());
        return enrichPost(post, userId);
    }

    public List<Map<String, Object>> getByUserId(Long userId, Long currentUserId) {
        QueryWrapper<Post> query = new QueryWrapper<>();
        query.eq("user_id", userId).orderByDesc("created_at");
        List<Post> posts = postMapper.selectList(query);
        return posts.stream()
                .map(post -> enrichPost(post, currentUserId))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getLikedByUserId(Long userId, Long currentUserId) {
        QueryWrapper<com.qingyu.entity.Like> likeQuery = new QueryWrapper<>();
        likeQuery.eq("user_id", userId).orderByDesc("created_at");
        List<com.qingyu.entity.Like> likes = likeMapper.selectList(likeQuery);

        List<Long> postIds = likes.stream()
                .map(com.qingyu.entity.Like::getPostId)
                .collect(Collectors.toList());

        if (postIds.isEmpty()) {
            return List.of();
        }

        List<Post> posts = postMapper.selectBatchIds(postIds);
        return posts.stream()
                .map(post -> enrichPost(post, currentUserId))
                .collect(Collectors.toList());
    }

    private Map<String, Object> enrichPost(Post post, Long currentUserId) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", post.getId());
        map.put("userId", post.getUserId());
        map.put("content", post.getContent());
        map.put("likeCount", post.getLikeCount());
        map.put("commentCount", post.getCommentCount());
        map.put("viewCount", post.getViewCount() != null ? post.getViewCount() : 0);
        map.put("createdAt", post.getCreatedAt());

        if (post.getImages() != null && !post.getImages().isEmpty()) {
            map.put("images", post.getImages());
        }

        User author = userMapper.selectById(post.getUserId());
        if (author != null) {
            map.put("nickname", author.getNickname());
            map.put("avatar", author.getAvatar());
        }

        if (currentUserId != null) {
            long likeCount = likeMapper.selectCount(
                new QueryWrapper<com.qingyu.entity.Like>()
                    .eq("post_id", post.getId())
                    .eq("user_id", currentUserId)
            );
            map.put("liked", likeCount > 0);
        } else {
            map.put("liked", false);
        }

        return map;
    }
}
