<template>
  <div class="notifications-page">
    <div class="notif-header">
      <h2 class="page-title">通知</h2>
      <button v-if="unreadCount > 0" class="btn-mark-all" @click="markAllRead">
        全部已读
      </button>
    </div>

    <div class="notif-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        class="notif-tab"
        :class="{ active: activeTab === tab.key }"
        @click="activeTab = tab.key"
      >
        {{ tab.label }}
      </button>
    </div>

    <LoadingDots v-if="loading" />

    <div v-else-if="filteredList.length" class="notif-list">
      <div
        v-for="item in filteredList"
        :key="item.id"
        class="notif-item"
        :class="{ unread: !item.isRead }"
        @click="handleClick(item)"
      >
        <div class="notif-avatar" :style="getAvatarStyle(item)">
          <span>{{ (item.fromNickname || 'U').charAt(0) }}</span>
        </div>
        <div class="notif-body">
          <p class="notif-text">
            <strong>{{ item.fromNickname }}</strong>
            <span v-if="item.type === 'like'"> 赞了你的轻语</span>
            <span v-else-if="item.type === 'comment'"> 评论了你的轻语</span>
            <span v-else>{{ item.content }}</span>
          </p>
          <span class="notif-time">{{ formatTime(item.createdAt) }}</span>
        </div>
        <div v-if="!item.isRead" class="notif-dot"></div>
      </div>
    </div>

    <div v-else class="notif-empty">
      <div class="empty-bell">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="var(--color-border)" stroke-width="1.5">
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
          <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
        </svg>
      </div>
      <p>暂时没有新通知</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { notificationApi } from '../api'
import { formatTime } from '../utils/time'
import LoadingDots from '../components/LoadingDots.vue'

const router = useRouter()

const tabs = [
  { key: 'all', label: '全部' },
  { key: 'like', label: '点赞' },
  { key: 'comment', label: '评论' }
]

const activeTab = ref('all')
const notifications = ref([])
const loading = ref(true)

const filteredList = computed(() => {
  if (activeTab.value === 'all') return notifications.value
  return notifications.value.filter(n => n.type === activeTab.value)
})

const unreadCount = computed(() => {
  return notifications.value.filter(n => !n.isRead).length
})

const getAvatarStyle = (item) => {
  const colors = ['#F2A7B0', '#A8D8EA', '#C3B1E1', '#B5EAD7', '#FFEAA7']
  const index = (item.fromUserId || 0) % colors.length
  return { background: colors[index] }
}

const handleClick = async (item) => {
  if (!item.isRead) {
    try {
      await notificationApi.markRead(item.id)
      item.isRead = 1
    } catch (e) {
      console.error(e)
    }
  }
  if (item.postId) {
    router.push(`/post/${item.postId}`)
  }
}

const markAllRead = async () => {
  try {
    await notificationApi.markAllRead()
    notifications.value.forEach(n => { n.isRead = 1 })
  } catch (e) {
    console.error(e)
  }
}

onMounted(async () => {
  try {
    const res = await notificationApi.getList()
    notifications.value = res.data
  } catch (e) {
    console.error('获取通知失败:', e)
  } finally {
    loading.value = false
  }
})
</script>

<style>
.page-title {
  font-size: 1.3rem;
  font-weight: 700;
  margin-bottom: 0;
}

.notif-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.btn-mark-all {
  padding: 6px 14px;
  border-radius: 14px;
  font-size: 0.8rem;
  background: rgba(168, 216, 234, 0.15);
  color: var(--color-blue);
  font-weight: 500;
}

.btn-mark-all:hover {
  background: rgba(168, 216, 234, 0.3);
}

.notif-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 20px;
  background: var(--color-card);
  border-radius: 24px;
  padding: 4px;
  box-shadow: var(--shadow-card);
}

.notif-tab {
  flex: 1;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.85rem;
  color: var(--color-text-light);
  background: transparent;
  font-weight: 500;
}

.notif-tab.active {
  background: var(--color-primary);
  color: white;
}

.notif-tab:hover:not(.active) {
  background: var(--color-hover);
}

.notif-list {
  background: var(--color-card);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-card);
  overflow: hidden;
}

.notif-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  cursor: pointer;
  transition: var(--transition);
  border-bottom: 1px solid var(--color-border);
}

.notif-item:last-child {
  border-bottom: none;
}

.notif-item:hover {
  background: var(--color-hover);
}

.notif-item.unread {
  background: rgba(242, 167, 176, 0.04);
}

.notif-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: white;
  font-weight: 600;
  font-size: 0.85rem;
}

.notif-body {
  flex: 1;
}

.notif-text {
  font-size: 0.9rem;
  color: var(--color-text);
  line-height: 1.4;
}

.notif-text strong {
  font-weight: 600;
}

.notif-time {
  font-size: 0.75rem;
  color: var(--color-text-light);
}

.notif-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--color-primary);
  flex-shrink: 0;
}

.notif-empty {
  text-align: center;
  padding: 60px 0;
  color: var(--color-text-light);
}

.empty-bell {
  margin-bottom: 12px;
}
</style>
