<template>
  <div class="post-detail-page">
    <button class="btn-back" @click="$router.back()">
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
        <path d="M19 12H5M12 19l-7-7 7-7"/>
      </svg>
      <span>返回</span>
    </button>

    <LoadingDots v-if="loading" text="加载中..." />

    <template v-else-if="post">
      <div class="detail-card">
        <div class="detail-header">
          <div class="detail-avatar" :style="avatarStyle">
            <img v-if="post.avatar" :src="post.avatar" alt="" />
            <span v-else class="avatar-letter">{{ initial }}</span>
          </div>
          <div class="detail-meta">
            <router-link :to="`/user/${post.userId}`" class="detail-nickname">{{ post.nickname }}</router-link>
            <span class="detail-time">{{ formatTime(post.createdAt) }}</span>
          </div>
          <button v-if="canEdit" class="post-edit" @click="handleEdit">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/>
              <path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/>
            </svg>
          </button>
          <button v-if="canDelete" class="post-delete" @click="showDeleteConfirm = true">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 6h18M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/>
            </svg>
          </button>
        </div>

        <div class="detail-content">
          <p v-html="renderContent(post.content)"></p>
        </div>

        <div v-if="post.images && post.images.length" class="detail-images">
          <img v-for="(img, i) in post.images" :key="i" :src="img" alt="" @click="previewImage = img" />
        </div>

        <div class="detail-actions">
          <button class="action-btn-lg" :class="{ active: post.liked }" @click="handleLike">
            <svg width="22" height="22" viewBox="0 0 24 24" :fill="post.liked ? 'var(--color-primary)' : 'none'" :stroke="post.liked ? 'var(--color-primary)' : 'currentColor'" stroke-width="2">
              <path d="M20.84 4.61a5.5 5.5 0 00-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 00-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 000-7.78z"/>
            </svg>
            <span>{{ post.likeCount || 0 }}</span>
          </button>
          <div class="action-divider"></div>
          <span class="comment-count-label">{{ comments.length }} 条评论</span>
        </div>
      </div>

      <div class="comment-section">
        <div class="comment-input-box">
          <textarea
            v-model="commentText"
            class="comment-input"
            placeholder="写下你的想法..."
            rows="2"
            @keydown.ctrl.enter="submitComment"
          ></textarea>
          <div v-if="commentImages.length" class="comment-image-preview">
            <div v-for="(img, i) in commentImages" :key="i" class="comment-preview-item">
              <img :src="img.url" alt="" />
              <button class="comment-remove-img" @click="removeCommentImage(i)">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="white" stroke="white" stroke-width="2">
                  <circle cx="12" cy="12" r="10" fill="rgba(0,0,0,0.5)" stroke="none"/>
                  <path d="M15 9l-6 6M9 9l6 6" stroke="white" stroke-linecap="round"/>
                </svg>
              </button>
            </div>
          </div>
          <div class="comment-input-footer">
            <div class="comment-footer-left">
              <label class="comment-img-btn" title="添加图片">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                  <rect x="3" y="3" width="18" height="18" rx="4"/>
                  <circle cx="8.5" cy="8.5" r="1.5" fill="currentColor"/>
                  <path d="M21 15l-5-5L5 21" stroke-linecap="round"/>
                </svg>
                <input type="file" accept="image/*" multiple hidden @change="handleCommentImageSelect" />
              </label>
              <span class="comment-hint">Ctrl + Enter 发送</span>
            </div>
            <button class="btn-comment-submit" :disabled="!canSubmitComment || submitting" @click="submitComment">
              <span v-if="submitting">发送中...</span>
              <span v-else>发送</span>
            </button>
          </div>
        </div>

        <div class="comment-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-avatar" :style="getCommentAvatarStyle(comment)">
              <img v-if="comment.avatar" :src="comment.avatar" alt="" />
              <span v-else>{{ (comment.nickname || 'U').charAt(0) }}</span>
            </div>
            <div class="comment-body">
              <div class="comment-header">
                <router-link :to="`/user/${comment.userId}`" class="comment-name">{{ comment.nickname }}</router-link>
                <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
              </div>
              <p class="comment-text">{{ comment.content }}</p>
              <div v-if="comment.parsedImages && comment.parsedImages.length" class="comment-images">
                <img v-for="(img, i) in comment.parsedImages" :key="i" :src="img" alt="" @click="previewImage = img" />
              </div>
            </div>
          </div>
          <p v-if="!comments.length" class="no-comments">还没有评论，来说两句吧</p>
        </div>
      </div>
    </template>

    <ConfirmDialog
      :visible="showDeleteConfirm"
      message="确认删除这条微博吗？"
      @confirm="confirmDelete"
      @cancel="showDeleteConfirm = false"
    />

    <Teleport to="body">
      <div v-if="previewImage" class="image-lightbox" @click="previewImage = null">
        <img :src="previewImage" alt="" @click.stop />
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { postApi, commentApi, likeApi } from '../api'
import { formatTime } from '../utils/time'
import LoadingDots from '../components/LoadingDots.vue'
import ConfirmDialog from '../components/ConfirmDialog.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const openEdit = inject('openEdit')

const post = ref(null)
const comments = ref([])
const loading = ref(true)
const commentText = ref('')
const commentImages = ref([])
const previewIndex = ref(-1)
const previewImage = ref(null)
const submitting = ref(false)
const showDeleteConfirm = ref(false)

const canSubmitComment = computed(() => {
  return commentText.value.trim().length > 0 || commentImages.value.length > 0
})

const initial = computed(() => {
  return (post.value?.nickname || 'U').charAt(0).toUpperCase()
})

const avatarStyle = computed(() => {
  if (post.value?.avatar) return {}
  const colors = ['#F2A7B0', '#A8D8EA', '#C3B1E1', '#B5EAD7', '#FFEAA7']
  const index = (post.value?.userId || 0) % colors.length
  return { background: colors[index] }
})

const getCommentAvatarStyle = (comment) => {
  if (comment.avatar) return {}
  const colors = ['#F2A7B0', '#A8D8EA', '#C3B1E1', '#B5EAD7', '#FFEAA7']
  const index = (comment.userId || 0) % colors.length
  return { background: colors[index] }
}

const renderContent = (content) => {
  if (!content) return ''
  return content.replace(/#([^#]+)#/g, (match, topic) => {
    return `<a class="topic-tag" href="/topic/${encodeURIComponent(topic)}">${match}</a>`
  })
}

const fetchData = async () => {
  loading.value = true
  try {
    const postId = route.params.id
    const [postRes, commentRes] = await Promise.all([
      postApi.getDetail(postId),
      commentApi.getList(postId)
    ])
    post.value = postRes.data
    if (post.value.images && typeof post.value.images === 'string') {
      try { post.value.images = JSON.parse(post.value.images) } catch (e) { post.value.images = null }
    }
    comments.value = (commentRes.data || []).map(parseCommentImages)
  } catch (e) {
    console.error('加载失败:', e)
  } finally {
    loading.value = false
  }
}

const parseCommentImages = (comment) => {
  let parsedImages = null
  if (comment.images) {
    try {
      parsedImages = typeof comment.images === 'string' ? JSON.parse(comment.images) : comment.images
    } catch (e) { parsedImages = null }
  }
  return { ...comment, parsedImages }
}

const handleCommentImageSelect = (e) => {
  const files = Array.from(e.target.files)
  if (commentImages.value.length + files.length > 3) {
    return
  }
  files.forEach(file => {
    if (file.size > 10 * 1024 * 1024) return
    commentImages.value.push({ file, url: URL.createObjectURL(file) })
  })
  e.target.value = ''
}

const removeCommentImage = (index) => {
  URL.revokeObjectURL(commentImages.value[index].url)
  commentImages.value.splice(index, 1)
}

const handleLike = async () => {
  try {
    const res = await likeApi.toggle(post.value.id)
    post.value.liked = res.data.liked
    post.value.likeCount = res.data.likeCount
  } catch (e) {
    console.error(e)
  }
}

const submitComment = async () => {
  if (!canSubmitComment.value || submitting.value) return
  submitting.value = true
  try {
    const imageFiles = commentImages.value.map(img => img.file)
    const res = await commentApi.create(post.value.id, {
      content: commentText.value,
      images: imageFiles
    })
    comments.value.unshift(parseCommentImages(res.data))
    commentText.value = ''
    commentImages.value.forEach(img => URL.revokeObjectURL(img.url))
    commentImages.value = []
    post.value.commentCount++
  } catch (e) {
    console.error('评论失败:', e)
  } finally {
    submitting.value = false
  }
}

const canEdit = computed(() => {
  return post.value && post.value.userId === userStore.userInfo?.id
})

const canDelete = computed(() => {
  return post.value && (post.value.userId === userStore.userInfo?.id || userStore.isAdmin)
})

const handleEdit = () => {
  openEdit(post.value)
}

const confirmDelete = async () => {
  showDeleteConfirm.value = false
  try {
    await postApi.remove(post.value.id)
    router.back()
  } catch (e) {
    console.error('删除失败:', e)
  }
}

onMounted(fetchData)
</script>

<style>
.post-detail-page {
  padding-top: 8px;
}

.btn-back {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  margin-bottom: 16px;
  border-radius: var(--radius-sm);
  color: var(--color-text-light);
  background: transparent;
  font-size: 0.9rem;
}

.btn-back:hover {
  color: var(--color-text);
  background: var(--color-card);
}

.detail-card {
  background: var(--color-card);
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-card);
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.detail-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.detail-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-avatar .avatar-letter {
  color: white;
  font-family: var(--font-mono);
  font-weight: 700;
  font-size: 1.2rem;
}

.detail-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.detail-nickname {
  font-weight: 600;
  color: var(--color-text);
}

.detail-nickname:hover {
  color: var(--color-primary);
}

.detail-time {
  font-size: 0.8rem;
  color: var(--color-text-light);
}

.detail-content {
  font-size: 1.05rem;
  line-height: 1.8;
  margin-bottom: 16px;
}

.detail-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 8px;
  margin-bottom: 16px;
}

.detail-images img {
  width: 100%;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: var(--transition);
}

.detail-images img:hover {
  transform: scale(1.02);
}

.detail-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--color-border);
}

.action-btn-lg {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 20px;
  background: transparent;
  color: var(--color-text-light);
  font-size: 0.9rem;
  transition: background 0.3s ease;
}

.action-btn-lg:hover {
  background: var(--color-hover);
}

.action-btn-lg.active {
  color: var(--color-primary);
}

.action-btn-lg svg {
  transition: none;
}

.action-btn-lg svg path {
  transition: none;
}

.action-divider {
  width: 1px;
  height: 20px;
  background: var(--color-border);
}

.comment-count-label {
  font-size: 0.85rem;
  color: var(--color-text-light);
}

.comment-section {
  margin-top: 20px;
}

.comment-input-box {
  background: var(--color-card);
  border-radius: var(--radius-md);
  padding: 16px;
  box-shadow: var(--shadow-card);
  margin-bottom: 16px;
}

.comment-input {
  width: 100%;
  resize: none;
  font-size: 0.9rem;
  line-height: 1.6;
  color: var(--color-text);
  margin-bottom: 10px;
}

.comment-input::placeholder {
  color: var(--color-text-light);
}

.comment-input-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.comment-footer-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.comment-img-btn {
  display: flex;
  align-items: center;
  padding: 4px;
  border-radius: var(--radius-sm);
  color: var(--color-text-light);
  cursor: pointer;
}

.comment-img-btn:hover {
  color: var(--color-primary);
  background: var(--color-hover);
}

.comment-image-preview {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
}

.comment-preview-item {
  position: relative;
  width: 60px;
  height: 60px;
  border-radius: var(--radius-sm);
  overflow: hidden;
}

.comment-preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.comment-remove-img {
  position: absolute;
  top: 2px;
  right: 2px;
  background: transparent;
  padding: 2px;
  border-radius: 50%;
  cursor: pointer;
}

.comment-images {
  display: flex;
  gap: 6px;
  margin-top: 8px;
}

.comment-images img {
  max-width: 120px;
  max-height: 90px;
  border-radius: var(--radius-sm);
  object-fit: cover;
  cursor: pointer;
}

.comment-images img:hover {
  opacity: 0.9;
}

.comment-hint {
  font-size: 0.75rem;
  color: var(--color-text-light);
}

.btn-comment-submit {
  padding: 8px 24px;
  background: var(--color-primary);
  color: white;
  border-radius: 16px;
  font-size: 0.85rem;
  font-weight: 600;
}

.btn-comment-submit:hover:not(:disabled) {
  background: #e8939d;
}

.btn-comment-submit:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.comment-list {
  background: var(--color-card);
  border-radius: var(--radius-md);
  padding: 8px 16px;
  box-shadow: var(--shadow-card);
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid var(--color-border);
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 0.8rem;
  color: white;
  font-weight: 600;
}

.comment-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.comment-body {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.comment-name {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--color-text);
}

.comment-name:hover {
  color: var(--color-primary);
}

.comment-time {
  font-size: 0.75rem;
  color: var(--color-text-light);
}

.comment-text {
  font-size: 0.9rem;
  line-height: 1.5;
  color: var(--color-text);
}

.no-comments {
  text-align: center;
  padding: 24px 0;
  color: var(--color-text-light);
  font-size: 0.9rem;
}

.image-lightbox {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  cursor: pointer;
  animation: fadeIn 0.2s ease;
}

.image-lightbox img {
  max-width: 60vw;
  max-height: 60vh;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  cursor: default;
}
</style>
