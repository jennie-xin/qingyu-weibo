package com.qingyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingyu.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PostMapper extends BaseMapper<Post> {

    @Update("UPDATE posts SET view_count = view_count + 1 WHERE id = #{postId}")
    void incrementViewCount(Long postId);
}
