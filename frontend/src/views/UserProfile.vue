<template>
  <div class="profile-page">
    <div class="page-nav">
      <button class="btn-back" @click="goBack">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
        <span>返回</span>
      </button>
      <span class="page-nav-title">个人主页</span>
    </div>

    <div class="profile-header">
      <div class="profile-cover">
        <img v-if="coverUrl" :src="coverUrl" alt="" class="cover-img" :style="coverStyle" />
        <div v-else class="cover-default" :style="coverBg">
          <span class="cover-letter">{{ initial }}</span>
        </div>
      </div>
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
      <div class="profile-tabs">
        <button :class="['tab-btn', { active: activeTab === 'posts' }]" @click="switchTab('posts')">TA的轻语</button>
        <button :class="['tab-btn', { active: activeTab === 'liked' }]" @click="switchTab('liked')">TA的点赞</button>
      </div>
      <LoadingDots v-if="loading" />
      <template v-else-if="displayPosts.length">
        <PostCard
          v-for="post in displayPosts"
          :key="post.id"
          :post="post"
          :current-user-id="currentUserId"
          :is-admin="isAdmin"
          @like="handleLike"
          @delete="handleDelete"
        />
      </template>
      <div v-else class="empty-posts">
        <p>{{ activeTab === 'posts' ? '还没有发布过轻语' : '还没有点赞过' }}</p>
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
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { userApi, postApi, likeApi } from '../api'
import PostCard from '../components/PostCard.vue'
import LoadingDots from '../components/LoadingDots.vue'
import EditProfileModal from '../components/EditProfileModal.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const coverUrl = computed(() => {
  if (!user.value.cover) return null
  const raw = user.value.cover
  return raw.split('#pos=')[0]
})

const coverStyle = computed(() => {
  if (!user.value.cover) return {}
  const match = user.value.cover.match(/#pos=(\d+)/)
  const pos = match ? match[1] : 50
  return { objectPosition: `center ${pos}%` }
})

const coverBg = computed(() => {
  const colors = ['#F2A7B0', '#A8D8EA', '#C3B1E1', '#B5EAD7', '#FFEAA7']
  const index = (user.value.id || 0) % colors.length
  return { background: `linear-gradient(135deg, ${colors[index]}, ${colors[(index + 2) % colors.length]})` }
})

const user = ref({})
const posts = ref([])
const likedPosts = ref([])
const stats = ref({ postCount: 0, likeCount: 0 })
const loading = ref(true)
const showEdit = ref(false)
const activeTab = ref('posts')

const displayPosts = computed(() => activeTab.value === 'posts' ? posts.value : likedPosts.value)

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

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}

const fetchUserData = async () => {
  loading.value = true
  try {
    const userId = route.params.id
    const res = await userApi.getInfo(userId)
    user.value = res.data.user
    posts.value = res.data.posts.map(p => {
      if (p.images && typeof p.images === 'string') {
        try { p.images = JSON.parse(p.images) } catch (e) { p.images = null }
      }
      return p
    })
    stats.value = res.data.stats
    const likedRes = await userApi.getLikedPosts(userId)
    likedPosts.value = likedRes.data.map(p => {
      if (p.images && typeof p.images === 'string') {
        try { p.images = JSON.parse(p.images) } catch (e) { p.images = null }
      }
      return p
    })
  } catch (e) {
    console.error('获取用户信息失败:', e)
  } finally {
    loading.value = false
  }
}

const switchTab = (tab) => {
  activeTab.value = tab
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

.page-nav {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding: 8px 0;
}

.page-nav .btn-back {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 20px;
  background: var(--color-card);
  border: 1.5px solid var(--color-border);
  color: var(--color-text);
  font-size: 0.85rem;
  cursor: pointer;
  transition: var(--transition);
}

.page-nav .btn-back:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.page-nav-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--color-text);
}

.profile-header {
  background: var(--color-card);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  margin-bottom: 24px;
}

.profile-cover {
  height: 160px;
  overflow: hidden;
  position: relative;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-default {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-letter {
  font-size: 4rem;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.6);
  font-family: var(--font-mono);
  user-select: none;
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

.profile-tabs {
  display: flex;
  gap: 0;
  margin-bottom: 20px;
  border-bottom: 1.5px solid var(--color-border);
}

.tab-btn {
  flex: 1;
  padding: 12px 0;
  background: none;
  border: none;
  font-size: 0.95rem;
  font-weight: 500;
  color: var(--color-text-light);
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: var(--transition);
}

.tab-btn.active {
  color: var(--color-primary);
  border-bottom-color: var(--color-primary);
  font-weight: 600;
}

.tab-btn:hover:not(.active) {
  color: var(--color-text);
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
