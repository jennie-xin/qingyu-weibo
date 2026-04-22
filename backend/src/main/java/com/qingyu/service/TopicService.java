package com.qingyu.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingyu.entity.Post;
import com.qingyu.entity.Topic;
import com.qingyu.entity.User;
import com.qingyu.mapper.PostMapper;
import com.qingyu.mapper.TopicMapper;
import com.qingyu.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TopicService {

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    private static final Pattern TOPIC_PATTERN = Pattern.compile("#([^#]+)#");

    public void extractAndSaveTopics(String content, Long postId) {
        Matcher matcher = TOPIC_PATTERN.matcher(content);
        while (matcher.find()) {
            String topicName = matcher.group(1).trim();
            if (topicName.isEmpty()) continue;

            Topic topic = topicMapper.selectOne(
                new QueryWrapper<Topic>().eq("name", topicName)
            );
            if (topic == null) {
                topic = new Topic();
                topic.setName(topicName);
                topic.setPostCount(1);
                topicMapper.insert(topic);
            } else {
                topic.setPostCount(topic.getPostCount() + 1);
                topicMapper.updateById(topic);
            }
        }
    }

    public List<Topic> getHotTopics() {
        QueryWrapper<Topic> query = new QueryWrapper<>();
        query.orderByDesc("post_count").last("LIMIT 10");
        return topicMapper.selectList(query);
    }

    public List<Map<String, Object>> getPostsByTopic(String topicName) {
        List<Post> allPosts = postMapper.selectList(
            new QueryWrapper<Post>().like("content", "#" + topicName + "#").orderByDesc("created_at")
        );

        return allPosts.stream().map(post -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("userId", post.getUserId());
            map.put("content", post.getContent());
            map.put("images", post.getImages());
            map.put("likeCount", post.getLikeCount());
            map.put("commentCount", post.getCommentCount());
            map.put("createdAt", post.getCreatedAt());

            User author = userMapper.selectById(post.getUserId());
            if (author != null) {
                map.put("nickname", author.getNickname());
                map.put("avatar", author.getAvatar());
            }
            return map;
        }).collect(Collectors.toList());
    }
}
