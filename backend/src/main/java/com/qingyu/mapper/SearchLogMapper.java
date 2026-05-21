package com.qingyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingyu.entity.SearchLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SearchLogMapper extends BaseMapper<SearchLog> {

    @Select("SELECT keyword, COUNT(*) as search_count FROM search_logs " +
            "WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            "GROUP BY keyword ORDER BY search_count DESC LIMIT 10")
    List<Map<String, Object>> getHotSearches();

    @Select("SELECT keyword, COUNT(*) as search_count FROM search_logs " +
            "GROUP BY keyword ORDER BY search_count DESC LIMIT 10")
    List<Map<String, Object>> getAllTimeHotSearches();
}
