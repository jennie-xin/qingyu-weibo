<template>
  <div class="search-page">
    <div class="page-nav">
      <button class="btn-back" @click="goBack">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
        <span>返回</span>
      </button>
      <span class="page-nav-title">搜索结果</span>
    </div>

    <div class="search-header">
      <h3 class="search-keyword">"{{ keyword }}"</h3>
      <span class="search-count" v-if="!loading">找到 {{ posts.length }} 条结果</span>
    </div>

    <LoadingDots v-if="loading" />
    <template v-else-if="posts.length">
      <PostCard
        v-for="post in posts"
        :key="post.id"
        :post="post"
        :current-user-id="currentUserId"
        :is-admin="isAdmin"
        @like="handleLike"
        @delete="handleDelete"
      />
    </template>
    <div v-else class="empty-search">
      <p>没有找到相关内容</p>
      <p class="empty-hint">换个关键词试试吧</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { searchApi, likeApi, postApi } from '../api'
import PostCard from '../components/PostCard.vue'
import LoadingDots from '../components/LoadingDots.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const posts = ref([])
const loading = ref(true)
const keyword = ref('')

const currentUserId = computed(() => userStore.userInfo?.id)
const isAdmin = computed(() => userStore.isAdmin)

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}

const fetchResults = async () => {
  const q = route.query.q
  if (!q) return
  keyword.value = q
  loading.value = true
  try {
    const res = await searchApi.search(q)
    posts.value = res.data.map(p => {
      if (p.images && typeof p.images === 'string') {
        try { p.images = JSON.parse(p.images) } catch (e) { p.images = null }
      }
      return p
    })
  } catch (e) {
    posts.value = []
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
      post.likeCount = res.data.likeCount
    }
  } catch (e) {
    console.error(e)
  }
}

const handleDelete = async (postId) => {
  try {
    await postApi.remove(postId)
    posts.value = posts.value.filter(p => p.id !== postId)
  } catch (e) {
    console.error(e)
  }
}

onMounted(fetchResults)
watch(() => route.query.q, fetchResults)
</script>

<style>
.search-page {
  padding-top: 0;
}

.search-page .page-nav {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding: 8px 0;
}

.search-header {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 20px;
}

.search-keyword {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--color-text);
}

.search-count {
  font-size: 0.85rem;
  color: var(--color-text-light);
}

.empty-search {
  text-align: center;
  padding: 60px 0;
  color: var(--color-text-light);
}

.empty-search p {
  font-size: 1rem;
  margin-bottom: 8px;
}

.empty-hint {
  font-size: 0.85rem;
}
</style>