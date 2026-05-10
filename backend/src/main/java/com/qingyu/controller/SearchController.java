package com.qingyu.controller;

import com.qingyu.common.Result;
import com.qingyu.service.SearchService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    public Result<List<Map<String, Object>>> search(@RequestParam String q, HttpServletRequest request) {
        if (q == null || q.trim().isEmpty()) {
            return Result.error("搜索关键词不能为空");
        }
        Long userId = (Long) request.getAttribute("userId");
        List<Map<String, Object>> results = searchService.search(q.trim(), userId);
        return Result.success(results);
    }

    @GetMapping("/hot")
    public Result<List<Map<String, Object>>> hotSearches() {
        List<Map<String, Object>> hot = searchService.getHotSearches();
        return Result.success(hot);
    }
}
