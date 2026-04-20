<template>
  <div class="topic-page">
    <div class="topic-header">
      <div class="topic-icon">
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="var(--color-purple)" stroke-width="1.8">
          <path d="M4 9h16M4 15h16M10 3L8 21M16 3l-2 18" stroke-linecap="round"/>
        </svg>
      </div>
      <div class="topic-info">
        <h2 class="topic-name">{{ topicName }}</h2>
        <p class="topic-count">{{ posts.length }} 条相关轻语</p>
      </div>
    </div>

    <LoadingDots v-if="loading" />

    <div v-else-if="posts.length" class="topic-feed">
      <PostCard
        v-for="post in posts"
        :key="post.id"
        :post="post"
        @like="handleLike"
      />
    </div>

    <div v-else class="topic-empty">
      <p>这个话题下还没有内容</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { topicApi, likeApi } from '../api'
import PostCard from '../components/PostCard.vue'
import LoadingDots from '../components/LoadingDots.vue'

const route = useRoute()
const posts = ref([])
const loading = ref(true)
const topicName = ref('')

const fetchTopic = async () => {
  loading.value = true
  topicName.value = decodeURIComponent(route.params.name)
  try {
    const res = await topicApi.getPosts(topicName.value)
    posts.value = res.data
  } catch (e) {
    console.error('获取话题失败:', e)
  } finally {
    loading.value = false
  }
}

const handleLike = async (postId) => {
  try {
    const res = await likeApi.toggle(postId)
    const post = posts.value.find(p => p.id === postId)
    if (post) {
      post.liked = res.data.liked
      post.likeCount += res.data.liked ? 1 : -1
    }
  } catch (e) {
    console.error(e)
  }
}

onMounted(fetchTopic)
watch(() => route.params.name, fetchTopic)
</script>

<style>
.topic-page {
  padding-top: 8px;
}

.topic-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: var(--color-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  margin-bottom: 20px;
}

.topic-icon {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: rgba(195, 177, 225, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
}

.topic-name {
  font-size: 1.2rem;
  font-weight: 700;
}

.topic-count {
  font-size: 0.85rem;
  color: var(--color-text-light);
  margin-top: 2px;
}

.topic-empty {
  text-align: center;
  padding: 60px 0;
  color: var(--color-text-light);
}
</style>
