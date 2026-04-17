package com.qingyu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("posts")
public class Post {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String content;
    private String images;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createdAt;
}
