[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/QLSGUFuT)
[![Open in Visual Studio Code](https://classroom.github.com/assets/open-in-vscode-2e0aaae1b6195c2367325f4f02e2d04e9abb55f0b24a779b69b11b9e10269abc.svg)](https://classroom.github.com/online_ide?assignment_repo_id=23359018&assignment_repo_type=AssignmentRepo)

# 轻语 (QingYu)

> 一个马卡龙配色的轻量微博平台，用文字记录生活的温度。

## 技术栈

- **前端**: Vue 3 + Vite + Vue Router + Pinia + Axios
- **后端**: Spring Boot 3.2 + MyBatis-Plus + JWT
- **数据库**: MySQL 8.0
- **样式**: 原生 CSS + CSS Variables（马卡龙配色系统）
- **字体**: JetBrains Mono + 系统中文字体

## 功能特性

### 核心功能
- 用户注册/登录（JWT 认证，SHA-256 加密）
- 发布微博（文字 + 多图上传，最多9张）
- 信息流分页浏览 + 骨架屏加载
- 评论系统（Ctrl+Enter 快捷发送）
- 点赞（带粒子爆炸特效 + 双击点赞大爱心动画）
- 话题标签 #话题# 自动提取与跳转
- 图片预览（全屏查看 + 键盘左右切换）

### 高级功能
- 通知系统（点赞/评论实时通知 + 一键全部已读）
- 权限控制（管理员/普通用户 + 用户封禁）
- 管理后台（用户管理 + 内容审核 + 数据统计 + 搜索过滤）
- 暗黑模式（一键切换 + localStorage 持久化）
- 个人主页（编辑资料 + 头像上传 + 注册时间）
- 表情选择器（32个常用 emoji）

### 体验优化
- 路由切换淡入动画 + 滚动位置记忆
- 响应式设计（移动端完整适配）
- 返回顶部按钮
- 通知轮询（30s 自动刷新）
- 点击外部关闭下拉菜单
- 404 页面
- 发布框字数统计 + 图片大小校验

## 本地开发

### 数据库

```bash
mysql -u root -p < backend/src/main/resources/schema.sql
```

### 后端

```bash
cd backend
mvn spring-boot:run
```

API 地址 http://localhost:8080

### 前端

```bash
cd frontend
npm install
npm run dev
```

访问 http://localhost:3000

## 项目结构

```
├── frontend/
│   ├── src/
│   │   ├── api/          # 接口封装
│   │   ├── assets/css/   # 全局样式 + CSS变量
│   │   ├── components/   # 通用组件
│   │   ├── router/       # 路由配置
│   │   ├── stores/       # Pinia 状态管理
│   │   ├── utils/        # 工具函数
│   │   └── views/        # 页面视图
│   └── vite.config.js
├── backend/
│   └── src/main/java/com/qingyu/
│       ├── common/       # 通用类（Result, JWT, 异常处理）
│       ├── config/       # 配置（CORS, 拦截器, MyBatis）
│       ├── controller/   # 控制器
│       ├── entity/       # 实体类
│       ├── mapper/       # MyBatis Mapper
│       └── service/      # 业务逻辑
└── README.md
```

## 默认账号

启动后需要自行注册，第一个注册的用户可以通过数据库手动设置为管理员：

```sql
UPDATE users SET role = 'admin' WHERE id = 1;
```
