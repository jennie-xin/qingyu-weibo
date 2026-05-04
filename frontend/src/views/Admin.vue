<template>
  <div class="admin-page">
    <h2 class="page-title">管理后台</h2>

    <div class="admin-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        class="admin-tab"
        :class="{ active: activeTab === tab.key }"
        @click="activeTab = tab.key"
      >
        {{ tab.label }}
      </button>
    </div>

    <div v-if="activeTab === 'users'" class="admin-section">
      <div class="admin-search">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/>
        </svg>
        <input v-model="userSearch" class="search-input" placeholder="搜索用户名或昵称..." />
      </div>
      <div class="admin-card">
        <table class="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>用户名</th>
              <th>昵称</th>
              <th>角色</th>
              <th>注册时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in filteredUsers" :key="user.id">
              <td class="td-id">{{ user.id }}</td>
              <td class="td-username">{{ user.username }}</td>
              <td>{{ user.nickname }}</td>
              <td>
                <span class="role-badge" :class="user.role">{{ user.role === 'admin' ? '管理员' : '用户' }}</span>
              </td>
              <td class="td-time">{{ formatTime(user.createdAt) }}</td>
              <td>
                <button
                  v-if="user.role !== 'admin'"
                  class="btn-sm btn-promote"
                  @click="promoteUser(user)"
                >
                  设为管理员
                </button>
                <span v-else class="text-muted">-</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="activeTab === 'posts'" class="admin-section">
      <div class="admin-search">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/>
        </svg>
        <input v-model="postSearch" class="search-input" placeholder="搜索内容或作者..." />
      </div>
      <div class="admin-card">
        <div v-for="post in filteredPosts" :key="post.id" class="admin-post-item">
          <div class="admin-post-info">
            <span class="admin-post-author">{{ post.nickname }}</span>
            <p class="admin-post-content">{{ post.content.substring(0, 80) }}{{ post.content.length > 80 ? '...' : '' }}</p>
            <span class="admin-post-time">{{ formatTime(post.createdAt) }}</span>
          </div>
          <button class="btn-sm btn-danger" @click="deletePost(post.id)">删除</button>
        </div>
        <p v-if="!allPosts.length" class="empty-text">暂无内容</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { adminApi, postApi } from '../api'
import { formatTime } from '../utils/time'

const tabs = [
  { key: 'users', label: '用户管理' },
  { key: 'posts', label: '内容审核' }
]

const activeTab = ref('users')
const users = ref([])
const allPosts = ref([])
const userSearch = ref('')
const postSearch = ref('')

const filteredUsers = computed(() => {
  if (!userSearch.value) return users.value
  const keyword = userSearch.value.toLowerCase()
  return users.value.filter(u =>
    u.username.toLowerCase().includes(keyword) ||
    (u.nickname || '').toLowerCase().includes(keyword)
  )
})

const filteredPosts = computed(() => {
  if (!postSearch.value) return allPosts.value
  const keyword = postSearch.value.toLowerCase()
  return allPosts.value.filter(p =>
    p.content.toLowerCase().includes(keyword) ||
    (p.nickname || '').toLowerCase().includes(keyword)
  )
})

const fetchUsers = async () => {
  try {
    const res = await adminApi.getUsers()
    users.value = res.data
  } catch (e) {
    console.error('获取用户列表失败:', e)
  }
}

const fetchPosts = async () => {
  try {
    const res = await postApi.getList({ page: 1, size: 50 })
    allPosts.value = res.data.records
  } catch (e) {
    console.error('获取微博列表失败:', e)
  }
}

const promoteUser = async (user) => {
  if (!confirm(`确定将 ${user.nickname} 设为管理员？`)) return
  try {
    await adminApi.updateUserRole(user.id, 'admin')
    user.role = 'admin'
  } catch (e) {
    console.error(e)
  }
}

const deletePost = async (postId) => {
  if (!confirm('确定删除这条微博？')) return
  try {
    await adminApi.deletePost(postId)
    allPosts.value = allPosts.value.filter(p => p.id !== postId)
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchUsers()
  fetchPosts()
})
</script>

<style>
.admin-page {
  padding-top: 8px;
}

.admin-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 20px;
  background: var(--color-card);
  border-radius: 24px;
  padding: 4px;
  box-shadow: var(--shadow-card);
}

.admin-tab {
  flex: 1;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.85rem;
  color: var(--color-text-light);
  background: transparent;
  font-weight: 500;
}

.admin-tab.active {
  background: var(--color-purple);
  color: white;
}

.admin-tab:hover:not(.active) {
  background: var(--color-hover);
}

.admin-card {
  background: var(--color-card);
  border-radius: var(--radius-md);
  padding: 20px;
  box-shadow: var(--shadow-card);
}

.admin-search {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: var(--color-card);
  border-radius: 20px;
  box-shadow: var(--shadow-card);
  margin-bottom: 16px;
  color: var(--color-text-light);
}

.search-input {
  flex: 1;
  font-size: 0.85rem;
  color: var(--color-text);
}

.search-input::placeholder {
  color: var(--color-text-light);
}

.admin-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.85rem;
}

.admin-table th {
  text-align: left;
  padding: 10px 8px;
  border-bottom: 2px solid var(--color-border);
  color: var(--color-text-light);
  font-weight: 600;
  font-size: 0.8rem;
}

.admin-table td {
  padding: 12px 8px;
  border-bottom: 1px solid var(--color-border);
}

.td-id {
  font-family: var(--font-mono);
  color: var(--color-text-light);
}

.td-username {
  font-family: var(--font-mono);
}

.td-time {
  font-size: 0.8rem;
  color: var(--color-text-light);
}

.role-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 0.75rem;
  font-weight: 600;
}

.role-badge.admin {
  background: rgba(195, 177, 225, 0.2);
  color: #9b59b6;
}

.role-badge.user {
  background: rgba(168, 216, 234, 0.2);
  color: #3498db;
}

.btn-sm {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
}

.btn-promote {
  background: rgba(195, 177, 225, 0.15);
  color: var(--color-purple);
}

.btn-promote:hover {
  background: rgba(195, 177, 225, 0.3);
}

.btn-danger {
  background: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
}

.btn-danger:hover {
  background: rgba(231, 76, 60, 0.2);
}

.admin-post-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 0;
  border-bottom: 1px solid var(--color-border);
}

.admin-post-item:last-child {
  border-bottom: none;
}

.admin-post-info {
  flex: 1;
}

.admin-post-author {
  font-size: 0.8rem;
  font-weight: 600;
  color: var(--color-text);
}

.admin-post-content {
  font-size: 0.85rem;
  color: var(--color-text-light);
  margin: 4px 0;
}

.admin-post-time {
  font-size: 0.75rem;
  color: var(--color-text-light);
}

.empty-text {
  text-align: center;
  padding: 24px;
  color: var(--color-text-light);
}

.text-muted {
  color: var(--color-text-light);
}
</style>
