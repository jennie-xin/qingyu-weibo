package com.qingyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingyu.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
