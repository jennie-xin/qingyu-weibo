[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/QLSGUFuT)
[![Open in Visual Studio Code](https://classroom.github.com/assets/open-in-vscode-2e0aaae1b6195c2367325f4f02e2d04e9abb55f0b24a779b69b11b9e10269abc.svg)](https://classroom.github.com/online_ide?assignment_repo_id=23359018&assignment_repo_type=AssignmentRepo)

# 轻语 (QingYu)

> 一个马卡龙配色的轻量微博平台，记录你的碎碎念。

## 技术栈

- **前端**: Vue 3 + Vite + Vue Router + Pinia + Axios
- **后端**: Spring Boot + MyBatis-Plus + JWT
- **数据库**: MySQL 8.0

## 本地开发

### 前端

```bash
cd frontend
npm install
npm run dev
```

访问 http://localhost:3000

### 后端

```bash
cd backend
mvn spring-boot:run
```

API 地址 http://localhost:8080

## 功能清单

- [x] 项目初始化
- [ ] 用户注册/登录（JWT认证）
- [ ] 发布微博（文字+图片）
- [ ] 信息流浏览
- [ ] 评论系统
- [ ] 点赞（带粒子特效）
- [ ] 话题标签 #话题#
- [ ] 权限控制（管理员/普通用户）
- [ ] 通知系统
- [ ] 管理后台
