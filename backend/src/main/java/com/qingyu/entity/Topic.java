package com.qingyu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("topics")
public class Topic {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer postCount;
    private LocalDateTime createdAt;
}
