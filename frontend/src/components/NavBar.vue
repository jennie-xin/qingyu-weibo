<template>
  <nav class="navbar">
    <div class="navbar-inner">
      <router-link to="/" class="navbar-brand">
        <span class="brand-dot"></span>
        轻语
      </router-link>

      <div class="navbar-actions" v-if="userStore.isLoggedIn">
        <router-link v-if="userStore.isAdmin" to="/admin" class="nav-icon" title="管理后台">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
            <path d="M12 15a3 3 0 100-6 3 3 0 000 6z"/>
            <path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 01-2.83 2.83l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-4 0v-.09a1.65 1.65 0 00-1-1.51 1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83-2.83l.06-.06a1.65 1.65 0 00.33-1.82 1.65 1.65 0 00-1.51-1H3a2 2 0 010-4h.09a1.65 1.65 0 001.51-1 1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 012.83-2.83l.06.06a1.65 1.65 0 001.82.33H9a1.65 1.65 0 001-1.51V3a2 2 0 014 0v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 2.83l-.06.06a1.65 1.65 0 00-.33 1.82V9a1.65 1.65 0 001.51 1H21a2 2 0 010 4h-.09a1.65 1.65 0 00-1.51 1z"/>
          </svg>
        </router-link>

        <router-link to="/notifications" class="nav-icon notif-icon" title="通知">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
            <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
          </svg>
          <span v-if="unreadCount > 0" class="notif-badge">{{ unreadCount > 9 ? '9+' : unreadCount }}</span>
        </router-link>

        <div class="nav-user-menu" ref="menuRef" @click="showMenu = !showMenu">
          <div class="nav-avatar" :style="avatarStyle">
            <img v-if="userStore.userInfo?.avatar" :src="userStore.userInfo.avatar" alt="" />
            <span v-else class="nav-avatar-letter">{{ initial }}</span>
          </div>
          <div v-if="showMenu" class="user-dropdown">
            <router-link :to="`/user/${userStore.userInfo?.id}`" class="dropdown-item" @click="showMenu = false">
              个人主页
            </router-link>
            <button class="dropdown-item" @click="handleLogout">退出登录</button>
          </div>
        </div>
      </div>

      <div class="navbar-actions" v-else>
        <router-link to="/login" class="btn-nav-login">登录</router-link>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { notificationApi } from '../api'

const router = useRouter()
const userStore = useUserStore()
const showMenu = ref(false)
const unreadCount = ref(0)
const menuRef = ref(null)
let pollTimer = null

const initial = computed(() => {
  return (userStore.userInfo?.nickname || 'U').charAt(0).toUpperCase()
})

const avatarStyle = computed(() => {
  if (userStore.userInfo?.avatar) return {}
  const colors = ['#F2A7B0', '#A8D8EA', '#C3B1E1', '#B5EAD7', '#FFEAA7']
  const index = (userStore.userInfo?.id || 0) % colors.length
  return { background: colors[index] }
})

const handleLogout = () => {
  userStore.logout()
  showMenu.value = false
  router.push('/login')
}

const fetchUnread = async () => {
  if (!userStore.isLoggedIn) return
  try {
    const res = await notificationApi.getList()
    unreadCount.value = res.data.filter(n => !n.isRead).length
  } catch (e) {
    // 静默失败
  }
}

const handleClickOutside = (e) => {
  if (menuRef.value && !menuRef.value.contains(e.target)) {
    showMenu.value = false
  }
}

onMounted(() => {
  if (userStore.isLoggedIn) {
    userStore.fetchProfile()
    fetchUnread()
    pollTimer = setInterval(fetchUnread, 30000)
  }
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer)
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(253, 246, 240, 0.85);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--color-border);
  transition: var(--transition);
}

.navbar-inner {
  max-width: 800px;
  margin: 0 auto;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.navbar-brand {
  font-family: var(--font-mono);
  font-size: 1.2rem;
  font-weight: 700;
  color: var(--color-text);
  display: flex;
  align-items: center;
  gap: 8px;
}

.navbar-brand:hover {
  color: var(--color-primary);
}

.brand-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary), var(--color-purple));
}

.navbar-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-icon {
  color: var(--color-text-light);
  display: flex;
  align-items: center;
  padding: 6px;
  border-radius: var(--radius-sm);
  position: relative;
}

.nav-icon:hover {
  color: var(--color-primary);
  background: var(--color-hover);
}

.notif-badge {
  position: absolute;
  top: -2px;
  right: -4px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  border-radius: 8px;
  background: var(--color-primary);
  color: white;
  font-size: 0.65rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-user-menu {
  position: relative;
  cursor: pointer;
}

.nav-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--transition);
}

.nav-avatar:hover {
  transform: scale(1.1);
}

.nav-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.nav-avatar-letter {
  color: white;
  font-family: var(--font-mono);
  font-weight: 700;
  font-size: 0.85rem;
}

.user-dropdown {
  position: absolute;
  top: 40px;
  right: 0;
  background: var(--color-card);
  border-radius: var(--radius-sm);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  min-width: 120px;
  overflow: hidden;
  animation: fadeIn 0.15s ease;
}

.dropdown-item {
  display: block;
  width: 100%;
  padding: 10px 16px;
  font-size: 0.85rem;
  color: var(--color-text);
  text-align: left;
  background: transparent;
  border: none;
  cursor: pointer;
}

.dropdown-item:hover {
  background: var(--color-hover);
  color: var(--color-primary);
}

.btn-nav-login {
  padding: 6px 18px;
  background: var(--color-primary);
  color: white;
  border-radius: 16px;
  font-size: 0.85rem;
  font-weight: 600;
}

.btn-nav-login:hover {
  background: #e8939d;
  color: white;
}
</style>
