package com.qingyu.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingyu.entity.Notification;
import com.qingyu.entity.User;
import com.qingyu.mapper.NotificationMapper;
import com.qingyu.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    public void createNotification(Long userId, Long fromUserId, String type, Long postId) {
        if (userId.equals(fromUserId)) return;

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setFromUserId(fromUserId);
        notification.setType(type);
        notification.setPostId(postId);
        notification.setIsRead(0);
        notificationMapper.insert(notification);
    }

    public List<Map<String, Object>> getByUserId(Long userId) {
        QueryWrapper<Notification> query = new QueryWrapper<>();
        query.eq("user_id", userId).orderByDesc("created_at");
        List<Notification> list = notificationMapper.selectList(query);

        return list.stream().map(n -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", n.getId());
            map.put("userId", n.getUserId());
            map.put("fromUserId", n.getFromUserId());
            map.put("type", n.getType());
            map.put("postId", n.getPostId());
            map.put("content", n.getContent());
            map.put("isRead", n.getIsRead());
            map.put("createdAt", n.getCreatedAt());

            User fromUser = userMapper.selectById(n.getFromUserId());
            if (fromUser != null) {
                map.put("fromNickname", fromUser.getNickname());
                map.put("fromAvatar", fromUser.getAvatar());
            }
            return map;
        }).collect(Collectors.toList());
    }

    public void markRead(Long notificationId, Long userId) {
        Notification n = notificationMapper.selectById(notificationId);
        if (n != null && n.getUserId().equals(userId)) {
            n.setIsRead(1);
            notificationMapper.updateById(n);
        }
    }
}
