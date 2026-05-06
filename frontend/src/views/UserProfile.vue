<template>
  <div class="profile-page">
    <div class="profile-header">
      <div class="profile-cover"></div>
      <div class="profile-info">
        <div class="profile-avatar" :style="avatarBg">
          <img v-if="user.avatar" :src="user.avatar" alt="" />
          <span v-else class="avatar-letter-lg">{{ initial }}</span>
        </div>
        <div class="profile-details">
          <h2 class="profile-name">{{ user.nickname || '加载中...' }}</h2>
          <p class="profile-username">@{{ user.username }}</p>
          <p class="profile-bio">{{ user.bio || '这个人很懒，什么都没写' }}</p>
          <p v-if="user.createdAt" class="profile-joined">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/>
            </svg>
            {{ joinDate }} 加入
          </p>
        </div>
        <button v-if="isOwner" class="btn-edit-profile" @click="showEdit = true">
          编辑资料
        </button>
      </div>
      <div class="profile-stats">
        <div class="stat-item">
          <span class="stat-num">{{ stats.postCount }}</span>
          <span class="stat-label">轻语</span>
        </div>
        <div class="stat-item">
          <span class="stat-num">{{ stats.likeCount }}</span>
          <span class="stat-label">获赞</span>
        </div>
      </div>
    </div>

    <div class="profile-posts">
      <h3 class="section-title">TA的轻语</h3>
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
      <div v-else class="empty-posts">
        <p>还没有发布过轻语</p>
      </div>
    </div>

    <EditProfileModal
      :visible="showEdit"
      :user="user"
      @close="showEdit = false"
      @updated="fetchUserData"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { userApi, postApi, likeApi } from '../api'
import PostCard from '../components/PostCard.vue'
import LoadingDots from '../components/LoadingDots.vue'
import EditProfileModal from '../components/EditProfileModal.vue'

const route = useRoute()
const userStore = useUserStore()

const user = ref({})
const posts = ref([])
const stats = ref({ postCount: 0, likeCount: 0 })
const loading = ref(true)
const showEdit = ref(false)

const currentUserId = computed(() => userStore.userInfo?.id)
const isAdmin = computed(() => userStore.isAdmin)
const isOwner = computed(() => {
  return userStore.userInfo?.id === Number(route.params.id)
})

const initial = computed(() => {
  return (user.value.nickname || 'U').charAt(0).toUpperCase()
})

const avatarBg = computed(() => {
  if (user.value.avatar) return {}
  const colors = ['#F2A7B0', '#A8D8EA', '#C3B1E1', '#B5EAD7', '#FFEAA7']
  const index = (user.value.id || 0) % colors.length
  return { background: colors[index] }
})

const joinDate = computed(() => {
  if (!user.value.createdAt) return ''
  const d = new Date(user.value.createdAt)
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
})

const fetchUserData = async () => {
  loading.value = true
  try {
    const userId = route.params.id
    const res = await userApi.getInfo(userId)
    user.value = res.data.user
    posts.value = res.data.posts
    stats.value = res.data.stats
  } catch (e) {
    console.error('获取用户信息失败:', e)
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

const handleDelete = async (postId) => {
  try {
    await postApi.remove(postId)
    posts.value = posts.value.filter(p => p.id !== postId)
    stats.value.postCount--
  } catch (e) {
    console.error(e)
  }
}

onMounted(fetchUserData)
watch(() => route.params.id, fetchUserData)
</script>

<style>
.profile-page {
  padding-top: 0;
}

.profile-header {
  background: var(--color-card);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  margin-bottom: 24px;
}

.profile-cover {
  height: 120px;
  background: linear-gradient(135deg, var(--color-primary), var(--color-blue), var(--color-purple));
  opacity: 0.8;
}

.profile-info {
  display: flex;
  align-items: flex-end;
  gap: 16px;
  padding: 0 24px;
  margin-top: -36px;
  position: relative;
}

.profile-avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  border: 4px solid var(--color-card);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.profile-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-letter-lg {
  color: white;
  font-family: var(--font-mono);
  font-weight: 700;
  font-size: 1.6rem;
}

.profile-details {
  flex: 1;
  padding-top: 40px;
}

.profile-name {
  font-size: 1.2rem;
  font-weight: 700;
}

.profile-username {
  font-size: 0.85rem;
  color: var(--color-text-light);
  font-family: var(--font-mono);
}

.profile-bio {
  margin-top: 6px;
  font-size: 0.9rem;
  color: var(--color-text);
}

.profile-joined {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 6px;
  font-size: 0.8rem;
  color: var(--color-text-light);
}

.btn-edit-profile {
  padding: 8px 20px;
  border: 1.5px solid var(--color-border);
  border-radius: 20px;
  background: transparent;
  font-size: 0.85rem;
  color: var(--color-text);
  white-space: nowrap;
}

.btn-edit-profile:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.profile-stats {
  display: flex;
  gap: 32px;
  padding: 20px 24px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-num {
  font-family: var(--font-mono);
  font-size: 1.2rem;
  font-weight: 700;
  color: var(--color-text);
}

.stat-label {
  font-size: 0.8rem;
  color: var(--color-text-light);
}

.section-title {
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: 16px;
  padding-left: 4px;
}

.empty-posts {
  text-align: center;
  padding: 40px 0;
  color: var(--color-text-light);
}

@media (max-width: 768px) {
  .profile-info {
    flex-direction: column;
    align-items: center;
    text-align: center;
    padding: 0 16px;
    margin-top: -36px;
  }

  .profile-details {
    padding-top: 8px;
  }

  .profile-stats {
    justify-content: center;
  }

  .btn-edit-profile {
    margin-top: 12px;
  }
}
</style>
