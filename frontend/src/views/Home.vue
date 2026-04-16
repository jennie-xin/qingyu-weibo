<template>
  <div class="home">
    <div class="publish-trigger" @click="showPublish = true">
      <div class="trigger-avatar">
        <div class="avatar-placeholder"></div>
      </div>
      <div class="trigger-input">今天想说点什么...</div>
    </div>
    <div class="feed">
      <p class="feed-empty">还没有人发言，来做第一个吧 ✨</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const showPublish = ref(false)
</script>

<style>
.home {
  padding-top: 16px;
}

.publish-trigger {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: var(--color-card);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-card);
  cursor: pointer;
  transition: var(--transition);
}

.publish-trigger:hover {
  box-shadow: var(--shadow-hover);
  transform: translateY(-2px);
}

.trigger-avatar .avatar-placeholder {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary), var(--color-purple));
}

.trigger-input {
  flex: 1;
  padding: 10px 16px;
  background: var(--color-bg);
  border-radius: 20px;
  color: var(--color-text-light);
  font-size: 0.9rem;
}

.feed {
  margin-top: 20px;
}

.feed-empty {
  text-align: center;
  color: var(--color-text-light);
  padding: 60px 0;
  font-size: 0.95rem;
}
</style>
