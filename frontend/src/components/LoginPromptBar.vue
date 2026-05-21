<template>
  <transition name="slide-down">
    <div v-if="visible" class="login-prompt-bar">
      <div class="login-prompt-inner">
        <span class="prompt-text">登录轻语，发现更多精彩内容</span>
        <div class="prompt-actions">
          <router-link to="/login" class="btn-prompt-login">登录</router-link>
          <router-link to="/register" class="btn-prompt-register">注册</router-link>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const visible = ref(false)

const handleScroll = () => {
  if (userStore.isLoggedIn) {
    visible.value = false
    return
  }
  visible.value = window.scrollY > 200
}

onMounted(() => window.addEventListener('scroll', handleScroll))
onUnmounted(() => window.removeEventListener('scroll', handleScroll))
</script>

<style>
.login-prompt-bar {
  position: fixed;
  top: 56px;
  left: 0;
  right: 0;
  z-index: 99;
  background: rgba(253, 246, 240, 0.95);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--color-border);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

[data-theme="dark"] .login-prompt-bar {
  background: rgba(30, 30, 30, 0.95);
}

.login-prompt-inner {
  max-width: 1240px;
  margin: 0 auto;
  padding: 10px 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
}

.prompt-text {
  font-size: 0.9rem;
  color: var(--color-text);
  font-weight: 500;
}

.prompt-actions {
  display: flex;
  gap: 8px;
}

.btn-prompt-login {
  padding: 6px 20px;
  background: var(--color-primary);
  color: white;
  border-radius: 16px;
  font-size: 0.85rem;
  font-weight: 600;
}

.btn-prompt-login:hover {
  background: #e8939d;
  color: white;
}

.btn-prompt-register {
  padding: 6px 20px;
  border: 1.5px solid var(--color-primary);
  color: var(--color-primary);
  border-radius: 16px;
  font-size: 0.85rem;
  font-weight: 600;
}

.btn-prompt-register:hover {
  background: rgba(242, 167, 176, 0.1);
}

.slide-down-enter-active,
.slide-down-leave-active {
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.slide-down-enter-from,
.slide-down-leave-to {
  transform: translateY(-100%);
  opacity: 0;
}
</style>
