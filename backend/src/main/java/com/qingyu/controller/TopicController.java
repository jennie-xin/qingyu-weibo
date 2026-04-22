package com.qingyu.controller;

import com.qingyu.common.Result;
import com.qingyu.entity.Topic;
import com.qingyu.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    @Autowired
    private TopicService topicService;

    @GetMapping("/hot")
    public Result<List<Topic>> hotTopics() {
        List<Topic> topics = topicService.getHotTopics();
        return Result.success(topics);
    }

    @GetMapping("/{name}")
    public Result<List<Map<String, Object>>> getByTopic(@PathVariable String name) {
        List<Map<String, Object>> posts = topicService.getPostsByTopic(name);
        return Result.success(posts);
    }
}
