<template>
  <div class="hot-topics" v-if="topics.length">
    <h4 class="sidebar-title">热门话题</h4>
    <div class="topic-list">
      <router-link
        v-for="topic in topics"
        :key="topic.id"
        :to="`/topic/${topic.name}`"
        class="topic-item"
      >
        <span class="topic-hash">#</span>
        <span class="topic-name">{{ topic.name }}</span>
        <span class="topic-heat">{{ topic.postCount }}条</span>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { topicApi } from '../api'

const topics = ref([])

onMounted(async () => {
  try {
    const res = await topicApi.getHot()
    topics.value = res.data
  } catch (e) {
    // 静默
  }
})
</script>

<style>
.hot-topics {
  background: var(--color-card);
  border-radius: var(--radius-md);
  padding: 18px;
  box-shadow: var(--shadow-card);
}

.sidebar-title {
  font-size: 0.9rem;
  font-weight: 700;
  margin-bottom: 12px;
  color: var(--color-text);
}

.topic-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.topic-item {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 10px;
  border-radius: var(--radius-sm);
  transition: var(--transition);
  font-size: 0.85rem;
}

.topic-item:hover {
  background: var(--color-hover);
}

.topic-hash {
  color: var(--color-blue);
  font-family: var(--font-mono);
  font-weight: 700;
}

.topic-name {
  flex: 1;
  color: var(--color-text);
  font-weight: 500;
}

.topic-item:hover .topic-name {
  color: var(--color-primary);
}

.topic-heat {
  font-size: 0.75rem;
  color: var(--color-text-light);
  font-family: var(--font-mono);
}
</style>
