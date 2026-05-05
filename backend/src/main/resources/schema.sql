-- 轻语微博 数据库初始化脚本
-- 先跑这个再启动后端

CREATE DATABASE IF NOT EXISTS qingyu DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE qingyu;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(32) NOT NULL UNIQUE COMMENT '登录用户名',
    password VARCHAR(128) NOT NULL COMMENT '加密后的密码',
    nickname VARCHAR(32) NOT NULL COMMENT '显示昵称',
    avatar VARCHAR(256) DEFAULT NULL COMMENT '头像URL，null则用生成的默认头像',
    bio VARCHAR(200) DEFAULT '' COMMENT '个人简介',
    role VARCHAR(16) DEFAULT 'user' COMMENT 'user/admin',
    status VARCHAR(16) DEFAULT 'active' COMMENT 'active/banned',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 微博/帖子表
CREATE TABLE IF NOT EXISTS posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '发布者ID',
    content TEXT NOT NULL COMMENT '微博正文',
    images VARCHAR(1024) DEFAULT NULL COMMENT '图片URL列表，JSON数组格式',
    like_count INT DEFAULT 0 COMMENT '点赞数冗余',
    comment_count INT DEFAULT 0 COMMENT '评论数冗余',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 评论表
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content VARCHAR(500) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 点赞表（联合唯一，防止重复点赞）
CREATE TABLE IF NOT EXISTS likes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_post_user (post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 话题表
CREATE TABLE IF NOT EXISTS topics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL UNIQUE COMMENT '话题名称',
    post_count INT DEFAULT 0 COMMENT '关联微博数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 微博-话题关联表
CREATE TABLE IF NOT EXISTS post_topics (
    post_id BIGINT NOT NULL,
    topic_id BIGINT NOT NULL,
    PRIMARY KEY (post_id, topic_id),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (topic_id) REFERENCES topics(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 通知表
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '接收通知的用户',
    from_user_id BIGINT NOT NULL COMMENT '触发通知的用户',
    type VARCHAR(16) NOT NULL COMMENT 'like/comment/system',
    post_id BIGINT DEFAULT NULL COMMENT '关联的微博',
    content VARCHAR(200) DEFAULT NULL,
    is_read TINYINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (from_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入一个默认管理员账号 密码是 admin123 (后端会用BCrypt加密，这里先放明文占位)
INSERT INTO users (username, password, nickname, role) VALUES
('admin', '$2a$10$placeholder', '管理员小轻', 'admin');
