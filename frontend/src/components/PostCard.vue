<template>
  <div class="post-card" @click="handleClick" @dblclick.stop="handleDblLike">
    <div class="post-card-header">
      <div class="post-avatar" :style="avatarStyle">
        <img v-if="post.avatar" :src="post.avatar" alt="" />
        <span v-else class="avatar-letter">{{ initial }}</span>
      </div>
      <div class="post-meta">
        <span class="post-nickname">{{ post.nickname }}</span>
        <span class="post-time">{{ formatTime(post.createdAt) }}</span>
      </div>
      <button v-if="canDelete" class="post-delete" @click.stop="handleDelete">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M3 6h18M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/>
        </svg>
      </button>
    </div>

    <div class="post-card-body" style="position: relative;">
      <p class="post-content" v-html="renderContent(post.content)"></p>
      <div v-if="post.images && post.images.length" class="post-images" :class="'img-count-' + post.images.length">
        <div v-for="(img, i) in post.images" :key="i" class="post-img-wrapper" @click.stop="previewImage(i)">
          <img :src="img" alt="" loading="lazy" />
        </div>
      </div>
      <svg v-if="showDblHeart" class="dbl-heart-anim" width="48" height="48" viewBox="0 0 24 24" fill="var(--color-primary)" stroke="none">
        <path d="M20.84 4.61a5.5 5.5 0 00-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 00-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 000-7.78z"/>
      </svg>
    </div>

    <div class="post-card-footer">
      <button class="action-btn like-btn" :class="{ 'is-liked': post.liked }" @click.stop="handleLike($event)">
        <svg class="like-icon" width="18" height="18" viewBox="0 0 24 24" :fill="post.liked ? 'var(--color-primary)' : 'none'" :stroke="post.liked ? 'var(--color-primary)' : 'currentColor'" stroke-width="2">
          <path d="M20.84 4.61a5.5 5.5 0 00-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 00-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 000-7.78z"/>
        </svg>
        <span :class="{ 'liked': post.liked }">{{ post.likeCount || '' }}</span>
      </button>
      <button class="action-btn" @click.stop="goDetail">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/>
        </svg>
        <span>{{ post.commentCount || '' }}</span>
      </button>
    </div>

    <ImagePreview
      :visible="showPreview"
      :images="post.images || []"
      :start-index="previewIndex"
      @close="showPreview = false"
    />
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { formatTime } from '../utils/time'
import { createLikeParticles } from '../utils/particles'
import ImagePreview from './ImagePreview.vue'

const props = defineProps({
  post: { type: Object, required: true },
  currentUserId: { type: Number, default: null },
  isAdmin: { type: Boolean, default: false }
})

const emit = defineEmits(['like', 'delete'])
const router = useRouter()
const showPreview = ref(false)
const previewIndex = ref(0)
const showDblHeart = ref(false)
let clickTimer = null

const initial = computed(() => {
  return (props.post.nickname || 'U').charAt(0).toUpperCase()
})

const avatarStyle = computed(() => {
  if (props.post.avatar) return {}
  const colors = ['#F2A7B0', '#A8D8EA', '#C3B1E1', '#B5EAD7', '#FFEAA7']
  const index = (props.post.userId || 0) % colors.length
  return { background: colors[index] }
})

const canDelete = computed(() => {
  return props.isAdmin || props.post.userId === props.currentUserId
})

const renderContent = (content) => {
  if (!content) return ''
  return content.replace(/#([^#]+)#/g, (match, topic) => {
    return `<a class="topic-tag" data-topic="${topic}" onclick="event.stopPropagation(); window.location.href='/topic/${encodeURIComponent(topic)}'">${match}</a>`
  })
}

const goDetail = () => {
  router.push(`/post/${props.post.id}`)
}

const handleDblLike = (event) => {
  if (clickTimer) {
    clearTimeout(clickTimer)
    clickTimer = null
  }
  if (props.post.liked) return
  showDblHeart.value = true
  setTimeout(() => { showDblHeart.value = false }, 800)
  createLikeParticles(event)
  emit('like', props.post.id)
}

const handleClick = () => {
  if (clickTimer) clearTimeout(clickTimer)
  clickTimer = setTimeout(() => {
    goDetail()
  }, 250)
}

const handleLike = (event) => {
  if (!props.post.liked) {
    createLikeParticles(event)
  }
  emit('like', props.post.id)
}

const handleDelete = () => {
  if (confirm('确定删除这条微博吗？')) {
    emit('delete', props.post.id)
  }
}

const previewImage = (index) => {
  previewIndex.value = index
  showPreview.value = true
}
</script>

<style>
.post-card {
  background: var(--color-card);
  border-radius: var(--radius-md);
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: var(--shadow-card);
  cursor: pointer;
  transition: var(--transition);
}

.post-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-hover);
}

.post-card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.post-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.post-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-letter {
  color: white;
  font-family: var(--font-mono);
  font-weight: 700;
  font-size: 1.1rem;
}

.post-meta {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.post-nickname {
  font-weight: 600;
  font-size: 0.95rem;
  color: var(--color-text);
}

.post-time {
  font-size: 0.8rem;
  color: var(--color-text-light);
}

.post-delete {
  padding: 6px;
  border-radius: var(--radius-sm);
  color: var(--color-text-light);
  background: transparent;
}

.post-delete:hover {
  color: #e74c3c;
  background: #ffeaea;
}

.post-card-body {
  margin-bottom: 14px;
}

.post-content {
  font-size: 0.95rem;
  line-height: 1.7;
  word-break: break-word;
}

.topic-tag {
  color: var(--color-blue);
  background: rgba(168, 216, 234, 0.15);
  padding: 1px 6px;
  border-radius: 10px;
  font-size: 0.9rem;
  cursor: pointer;
}

.topic-tag:hover {
  background: rgba(168, 216, 234, 0.3);
}

.post-images {
  display: grid;
  gap: 6px;
  margin-top: 12px;
  border-radius: var(--radius-sm);
  overflow: hidden;
}

.img-count-1 {
  grid-template-columns: 1fr;
  max-width: 360px;
}

.img-count-2 {
  grid-template-columns: 1fr 1fr;
}

.img-count-3 {
  grid-template-columns: 1fr 1fr 1fr;
}

.post-img-wrapper {
  aspect-ratio: 1;
  overflow: hidden;
  border-radius: 6px;
}

.img-count-1 .post-img-wrapper {
  aspect-ratio: auto;
  max-height: 300px;
}

.post-img-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.post-img-wrapper:hover img {
  transform: scale(1.05);
}

.post-card-footer {
  display: flex;
  gap: 24px;
  padding-top: 12px;
  border-top: 1px solid var(--color-border);
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 8px;
  border-radius: var(--radius-sm);
  color: var(--color-text-light);
  background: transparent;
  font-size: 0.85rem;
}

.action-btn:hover {
  color: var(--color-primary);
  background: var(--color-hover);
}

.action-btn .liked {
  color: var(--color-primary);
  font-weight: 600;
}

.like-btn.is-liked .like-icon {
  animation: heartBeat 0.4s cubic-bezier(0.17, 0.89, 0.32, 1.49);
}

@keyframes heartBeat {
  0% { transform: scale(1); }
  25% { transform: scale(1.3); }
  50% { transform: scale(0.9); }
  100% { transform: scale(1); }
}

.dbl-heart-anim {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) scale(0);
  animation: dblHeartPop 0.8s cubic-bezier(0.17, 0.89, 0.32, 1.49) forwards;
  pointer-events: none;
  opacity: 0.9;
}

@keyframes dblHeartPop {
  0% { transform: translate(-50%, -50%) scale(0); opacity: 0.9; }
  30% { transform: translate(-50%, -50%) scale(1.4); opacity: 0.9; }
  60% { transform: translate(-50%, -50%) scale(1.1); opacity: 0.7; }
  100% { transform: translate(-50%, -50%) scale(1.2); opacity: 0; }
}
</style>
