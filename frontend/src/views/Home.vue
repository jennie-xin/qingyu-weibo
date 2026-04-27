<template>
  <div class="home">
    <div class="home-layout">
      <div class="home-main">
        <div class="publish-trigger" @click="showPublish = true">
          <div class="trigger-avatar">
            <div class="avatar-placeholder"></div>
          </div>
          <div class="trigger-input">今天想说点什么...</div>
          <button class="trigger-btn">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2.5" stroke-linecap="round">
              <path d="M12 5v14M5 12h14"/>
            </svg>
          </button>
        </div>

        <div class="feed">
          <template v-if="postStore.loading">
            <SkeletonCard v-for="i in 3" :key="i" />
          </template>
          <template v-else-if="postStore.posts.length">
            <PostCard
              v-for="post in postStore.posts"
              :key="post.id"
              :post="post"
              :current-user-id="userStore.userInfo?.id"
              :is-admin="userStore.isAdmin"
              @like="handleLike"
              @delete="handleDelete"
            />
            <div v-if="postStore.hasMore" class="load-more">
              <button class="btn-load-more" @click="loadMore">加载更多</button>
            </div>
            <p v-else class="feed-end">没有更多了 ~</p>
          </template>
          <div v-else class="feed-empty-state">
            <div class="empty-illustration">
              <div class="empty-circle"></div>
              <div class="empty-line"></div>
              <div class="empty-line short"></div>
            </div>
            <p>还没有人发言，来做第一个吧</p>
          </div>
        </div>
      </div>

      <aside class="home-sidebar">
        <HotTopics />
      </aside>
    </div>

    <PublishModal
      :visible="showPublish"
      @close="showPublish = false"
      @publish="handlePublish"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { usePostStore } from '../stores/post'
import { useUserStore } from '../stores/user'
import { postApi, likeApi, uploadApi } from '../api'
import PostCard from '../components/PostCard.vue'
import PublishModal from '../components/PublishModal.vue'
import LoadingDots from '../components/LoadingDots.vue'
import HotTopics from '../components/HotTopics.vue'
import SkeletonCard from '../components/SkeletonCard.vue'

const postStore = usePostStore()
const userStore = useUserStore()
const showPublish = ref(false)

onMounted(() => {
  postStore.fetchPosts(1)
})

const loadMore = () => {
  postStore.fetchPosts(postStore.currentPage + 1)
}

const handleLike = async (postId) => {
  try {
    const res = await likeApi.toggle(postId)
    postStore.toggleLike(postId, res.data.liked)
  } catch (e) {
    console.error('点赞失败:', e)
  }
}

const handleDelete = async (postId) => {
  try {
    await postApi.remove(postId)
    postStore.removePost(postId)
  } catch (e) {
    console.error('删除失败:', e)
  }
}

const handlePublish = async ({ content, images }) => {
  let imageUrls = []
  if (images.length) {
    for (const file of images) {
      const res = await uploadApi.image(file)
      imageUrls.push(res.data)
    }
  }
  const res = await postApi.create({
    content,
    images: imageUrls.length ? JSON.stringify(imageUrls) : null
  })
  postStore.addPost(res.data)
}
</script>

<style>
.home {
  padding-top: 16px;
}

.home-layout {
  display: grid;
  grid-template-columns: 1fr 240px;
  gap: 24px;
  align-items: start;
}

.home-sidebar {
  position: sticky;
  top: 80px;
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
</style>
