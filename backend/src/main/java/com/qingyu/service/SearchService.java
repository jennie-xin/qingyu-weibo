package com.qingyu.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingyu.entity.Post;
import com.qingyu.entity.SearchLog;
import com.qingyu.entity.User;
import com.qingyu.mapper.LikeMapper;
import com.qingyu.mapper.PostMapper;
import com.qingyu.mapper.SearchLogMapper;
import com.qingyu.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private SearchLogMapper searchLogMapper;

    public List<Map<String, Object>> search(String keyword, Long currentUserId) {
        SearchLog log = new SearchLog();
        log.setKeyword(keyword);
        log.setUserId(currentUserId);
        searchLogMapper.insert(log);

        List<Post> posts = postMapper.selectList(
            new QueryWrapper<Post>().like("content", keyword).orderByDesc("created_at")
        );

        return posts.stream().map(post -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("userId", post.getUserId());
            map.put("content", post.getContent());
            map.put("images", post.getImages());
            map.put("likeCount", post.getLikeCount());
            map.put("commentCount", post.getCommentCount());
            map.put("viewCount", post.getViewCount() != null ? post.getViewCount() : 0);
            map.put("createdAt", post.getCreatedAt());

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
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getHotSearches() {
        return searchLogMapper.getHotSearches();
    }
}
