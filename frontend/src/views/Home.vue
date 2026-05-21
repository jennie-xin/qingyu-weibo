<template>
  <div class="home">
    <div class="home-layout">
      <LeftSidebar class="home-left-sidebar" @select="handleChannelSelect" />

      <div class="home-main">
        <div class="publish-trigger" @click="openPublish">
          <div class="trigger-input">今天想说点什么...</div>
          <button class="trigger-btn">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2.5" stroke-linecap="round">
              <path d="M12 5v14M5 12h14"/>
            </svg>
          </button>
        </div>

        <div class="feed">
          <template v-if="isLoading">
            <SkeletonCard v-for="i in 3" :key="i" />
          </template>
          <template v-else-if="displayPosts.length">
            <PostCard
              v-for="post in displayPosts"
              :key="post.id"
              :post="post"
              :current-user-id="userStore.userInfo?.id"
              :is-admin="userStore.isAdmin"
              @like="handleLike"
              @delete="handleDelete"
              @edit="handleEditClick"
            />
            <div v-if="!currentTopic && postStore.hasMore" class="load-more">
              <button class="btn-load-more" :disabled="loadingMore" @click="loadMore">
                <span v-if="loadingMore" class="loading-dots-sm"><i></i><i></i><i></i></span>
                <span v-else>加载更多</span>
              </button>
            </div>
            <p v-else-if="!currentTopic" class="feed-end">没有更多了 ~</p>
          </template>
          <div v-else class="feed-empty-state">
            <div class="empty-illustration">
              <div class="empty-circle"></div>
              <div class="empty-line"></div>
              <div class="empty-line short"></div>
            </div>
            <p>{{ currentTopic ? '该频道暂无内容' : '还没有人发言，来做第一个吧' }}</p>
          </div>
        </div>
      </div>

      <aside class="home-sidebar">
        <HotTopics />
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue'
import { usePostStore } from '../stores/post'
import { useUserStore } from '../stores/user'
import { postApi, likeApi, topicApi } from '../api'
import PostCard from '../components/PostCard.vue'
import LoadingDots from '../components/LoadingDots.vue'
import HotTopics from '../components/HotTopics.vue'
import SkeletonCard from '../components/SkeletonCard.vue'
import LeftSidebar from '../components/LeftSidebar.vue'

const postStore = usePostStore()
const userStore = useUserStore()
const loadingMore = ref(false)
const currentTopic = ref(null)
const topicPosts = ref([])
const topicLoading = ref(false)

const openPublish = inject('openPublish')
const openEdit = inject('openEdit')

const displayPosts = computed(() => {
  if (currentTopic.value) return topicPosts.value
  return postStore.posts
})

const isLoading = computed(() => {
  if (currentTopic.value) return topicLoading.value
  return postStore.loading
})

onMounted(() => {
  postStore.fetchPosts(1)
})

const handleChannelSelect = async (topic) => {
  currentTopic.value = topic
  if (!topic) return
  topicLoading.value = true
  try {
    const res = await topicApi.getPosts(topic)
    topicPosts.value = res.data.map(p => {
      if (p.images && typeof p.images === 'string') {
        try { p.images = JSON.parse(p.images) } catch (e) { p.images = null }
      }
      return p
    })
  } catch (e) {
    topicPosts.value = []
  } finally {
    topicLoading.value = false
  }
}

const loadMore = async () => {
  loadingMore.value = true
  await postStore.fetchPosts(postStore.currentPage + 1)
  loadingMore.value = false
}

const handleLike = async (postId) => {
  try {
    const res = await likeApi.toggle(postId)
    if (currentTopic.value) {
      const post = topicPosts.value.find(p => p.id === postId)
      if (post) {
        post.liked = res.data.liked
        post.likeCount = res.data.likeCount
      }
    } else {
      postStore.toggleLike(postId, res.data.liked, res.data.likeCount)
    }
  } catch (e) {
    console.error('点赞失败:', e)
  }
}

const handleDelete = async (postId) => {
  try {
    await postApi.remove(postId)
    if (currentTopic.value) {
      topicPosts.value = topicPosts.value.filter(p => p.id !== postId)
    } else {
      postStore.removePost(postId)
    }
  } catch (e) {
    console.error('删除失败:', e)
  }
}

const handleEditClick = (post) => {
  openEdit(post)
}
</script>

<style>
.home {
  padding-top: 16px;
}

.home-layout {
  display: grid;
  grid-template-columns: 200px 1fr 260px;
  gap: 24px;
  align-items: start;
}

.home-left-sidebar {
  position: sticky;
  top: 80px;
}

.home-sidebar {
  position: sticky;
  top: 80px;
}

@media (max-width: 1024px) {
  .home-layout {
    grid-template-columns: 1fr 240px;
  }
  .home-left-sidebar {
    display: none;
  }
}

@media (max-width: 768px) {
  .home-layout {
    grid-template-columns: 1fr;
  }
  .home-sidebar {
    display: none;
  }
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

.trigger-input {
  flex: 1;
  padding: 10px 16px;
  background: var(--color-bg);
  border-radius: 20px;
  color: var(--color-text-light);
  font-size: 0.9rem;
}

.trigger-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.trigger-btn:hover {
  background: #e8939d;
  transform: scale(1.1);
}

.feed {
  margin-top: 20px;
}

.feed-empty-state {
  text-align: center;
  padding: 60px 0;
  color: var(--color-text-light);
}

.empty-illustration {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.empty-circle {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  border: 3px dashed var(--color-border);
}

.empty-line {
  width: 80px;
  height: 8px;
  border-radius: 4px;
  background: var(--color-border);
}

.empty-line.short {
  width: 50px;
}

.load-more {
  text-align: center;
  padding: 20px 0;
}

.btn-load-more {
  padding: 10px 28px;
  background: transparent;
  border: 1.5px solid var(--color-border);
  border-radius: 20px;
  color: var(--color-text-light);
  font-size: 0.85rem;
}

.btn-load-more:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: var(--color-hover);
}

.feed-end {
  text-align: center;
  padding: 20px 0;
  font-size: 0.85rem;
  color: var(--color-text-light);
}

.loading-dots-sm {
  display: inline-flex;
  gap: 3px;
  align-items: center;
}

.loading-dots-sm i {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: var(--color-text-light);
  animation: dotBounce 0.6s infinite alternate;
}

.loading-dots-sm i:nth-child(2) { animation-delay: 0.2s; }
.loading-dots-sm i:nth-child(3) { animation-delay: 0.4s; }

@keyframes dotBounce {
  from { transform: translateY(0); }
  to { transform: translateY(-3px); }
}
</style>
