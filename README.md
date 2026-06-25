# 轻语 QingYu — 轻量级微博平台

> 用文字和图片，轻声记录生活的每一刻。

轻语是一个全栈微博社交平台，采用马卡龙配色设计风格，支持发帖、评论、点赞、话题、搜索、通知、暗黑模式等完整社交功能。

![Vue](https://img.shields.io/badge/Vue-3.4-4FC08D?logo=vue.js)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-6DB33F?logo=spring-boot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-5.2-646CFF?logo=vite&logoColor=white)

---

## 功能特性

### 用户系统
- 注册 / 登录（JWT 鉴权，7 天有效期）
- 个人资料编辑（昵称、头像、简介、封面图）
- 用户主页（发帖列表、点赞列表、个人信息）

### 动态发布
- 支持纯文字 + 多图（最多 9 张）发帖
- 字数限制 500 字，超过 450 字实时提醒
- Emoji 表情选择器
- 动态编辑 / 删除
- 全局发布入口，随时随地发动态

### 互动功能
- 点赞（带粒子爆炸动画 + 双击大爱心特效）
- 评论（支持文字 + 图片，Ctrl+Enter 快捷提交）
- 浏览量统计

### 话题系统
- 发帖自动提取 `#话题#` 标签
- 热门话题榜（按帖子数排序 Top 10）
- 话题详情页（聚合该话题下所有动态）

### 搜索
- 全文搜索动态内容
- 热搜榜（基于搜索频次排序）
- 搜索记录追踪

### 通知中心
- 点赞 / 评论实时通知（30 秒轮询）
- 未读消息提醒
- 单条已读 / 全部已读
- 按类型筛选通知

### 管理后台
- 数据统计面板（用户数、帖子数、今日发帖）
- 用户管理（搜索、封禁、提升管理员）
- 内容审核（删帖）

### UI / UX
- 马卡龙配色主题（暖奶油底色 + 柔和色彩）
- 暗黑模式切换（localStorage 持久化）
- 骨架屏加载占位
- 路由切换淡入动画
- 滚动位置记忆
- 图片预览（键盘左右切换）
- 响应式布局（适配移动端）
- 404 页面

---

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端框架 | Vue 3 + Composition API |
| 构建工具 | Vite 5 |
| 路由 | Vue Router 4 |
| 状态管理 | Pinia |
| HTTP 请求 | Axios |
| 后端框架 | Spring Boot 3.4 |
| ORM | MyBatis-Plus 3.5 |
| 数据库 | MySQL 8.0 |
| 认证 | JWT (jjwt 0.12) |
| Java 版本 | JDK 25 |

---

## 项目结构

```
├── frontend/                   # 前端 Vue 3 项目
│   ├── src/
│   │   ├── api/                # API 接口定义 + Axios 封装
│   │   ├── assets/css/         # 全局样式 + CSS 变量
│   │   ├── components/         # 26 个可复用组件
│   │   ├── router/             # 路由配置 + 导航守卫
│   │   ├── stores/             # Pinia 状态管理
│   │   ├── utils/              # 工具函数（粒子动画、时间格式化、Toast）
│   │   └── views/              # 10 个页面视图
│   └── vite.config.js          # Vite 配置 + 代理
│
├── backend/                    # 后端 Spring Boot 项目
│   └── src/main/java/com/qingyu/
│       ├── common/             # JWT 工具类 + 统一响应
│       ├── config/             # CORS、拦截器、MyBatis 配置
│       ├── controller/         # 10 个 REST 控制器
│       ├── entity/             # 7 个数据实体
│       ├── mapper/             # MyBatis-Plus Mapper
│       ├── service/            # 业务逻辑层
│       └── resources/
│           ├── schema.sql      # 数据库建表脚本
│           └── data-seed.sql   # 示例数据（15 用户 + 40 帖子）
```

---

## 快速开始

### 环境要求

- Node.js 18+
- JDK 25
- MySQL 8.0
- Maven 3.9+

### 1. 初始化数据库

```bash
mysql -u root -p < backend/src/main/resources/schema.sql
# 可选：导入示例数据
mysql -u root -p qingyu < backend/src/main/resources/data-seed.sql
```

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
# 服务运行在 http://localhost:8081
```

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
# 开发服务器运行在 http://localhost:3000
```

Vite 开发服务器已配置代理，`/api` 和 `/uploads` 请求自动转发到后端。

### 4. 访问应用

浏览器打开 http://localhost:3000

**默认管理员账号：**
- 用户名：`xiaoming`
- 密码：`123456`

---

## API 接口概览

| 模块 | 端点 | 说明 |
|------|------|------|
| 认证 | `POST /api/auth/login` | 登录 |
| | `POST /api/auth/register` | 注册 |
| | `GET /api/auth/profile` | 获取当前用户信息 |
| 动态 | `GET /api/posts` | 分页获取动态列表 |
| | `POST /api/posts` | 发布动态 |
| | `PUT /api/posts/:id` | 编辑动态 |
| | `DELETE /api/posts/:id` | 删除动态 |
| 评论 | `GET /api/posts/:id/comments` | 获取评论列表 |
| | `POST /api/posts/:id/comments` | 发表评论 |
| 点赞 | `POST /api/posts/:id/like` | 点赞/取消点赞 |
| 通知 | `GET /api/notifications` | 获取通知列表 |
| | `PUT /api/notifications/read-all` | 全部已读 |
| 搜索 | `GET /api/search?q=关键词` | 搜索动态 |
| | `GET /api/search/hot` | 热搜榜 |
| 话题 | `GET /api/topics/hot` | 热门话题 |
| | `GET /api/topics/:name` | 话题动态 |
| 管理 | `GET /api/admin/users` | 用户列表 |
| | `PUT /api/admin/users/:id/status` | 封禁/解封 |
| | `DELETE /api/admin/posts/:id` | 删除动态 |

---

## 数据库设计

共 8 张核心表：

- **users** — 用户表（角色：admin/user，状态：active/banned）
- **posts** — 动态表（内容 + 图片 JSON 数组 + 计数器）
- **comments** — 评论表（支持图片）
- **likes** — 点赞表（联合唯一约束防重复）
- **topics** — 话题表（名称唯一 + 帖子计数）
- **post_topics** — 动态-话题关联表
- **notifications** — 通知表（类型：like/comment/system）
- **search_logs** — 搜索日志表（用于热搜统计）

所有外键使用 `ON DELETE CASCADE` 保证数据一致性。

---

## 设计亮点

**马卡龙配色系统**
```css
--color-bg: #FDF6F0        /* 暖奶油背景 */
--color-primary: #F2A7B0   /* 烟粉色主色 */
--color-blue: #A8D8EA      /* 天空蓝 */
--color-purple: #C3B1E1    /* 薰衣草紫 */
--color-green: #B5EAD7     /* 薄荷绿 */
--color-yellow: #FFEAA7    /* 奶黄色 */
```

**交互细节**
- 点赞粒子爆炸动画（Canvas 绘制）
- 双击帖子触发大爱心飘出特效
- 骨架屏渐显加载体验
- 路由切换 fade-in 过渡
- 暗黑模式全局适配

---

## 开发者

- **jennie-xin** — 全栈开发

---

## 许可证

本项目仅用于学习交流，不得用于商业用途。
