<template>
  <Teleport to="body">
    <div v-if="visible" class="modal-overlay" @click.self="close">
      <div class="publish-modal">
        <div class="modal-header">
          <h3>发布轻语</h3>
          <button class="modal-close" @click="close">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
              <circle cx="12" cy="12" r="10" stroke-width="1.5" opacity="0.3"/>
              <path d="M15 9l-6 6M9 9l6 6"/>
            </svg>
          </button>
        </div>

        <div class="modal-body">
          <textarea
            ref="textareaRef"
            v-model="content"
            class="publish-textarea"
            placeholder="此刻的想法..."
            maxlength="500"
            @input="autoResize"
          ></textarea>

          <div v-if="images.length" class="image-preview-list">
            <div v-for="(img, i) in images" :key="i" class="preview-item">
              <img :src="img.url" alt="" />
              <button class="remove-img" @click="removeImage(i)">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="white" stroke="white" stroke-width="2">
                  <circle cx="12" cy="12" r="10" fill="rgba(0,0,0,0.5)" stroke="none"/>
                  <path d="M15 9l-6 6M9 9l6 6" stroke="white" stroke-linecap="round"/>
                </svg>
              </button>
            </div>
          </div>

          <div class="publish-toolbar">
            <label class="toolbar-btn" title="添加图片">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="var(--color-blue)" stroke-width="1.8">
                <rect x="3" y="3" width="18" height="18" rx="4"/>
                <circle cx="8.5" cy="8.5" r="1.5" fill="var(--color-blue)"/>
                <path d="M21 15l-5-5L5 21" stroke-linecap="round"/>
              </svg>
              <input type="file" accept="image/*" multiple hidden @change="handleImageSelect" />
            </label>
            <button class="toolbar-btn" title="插入话题" @click="insertTopic">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="var(--color-purple)" stroke-width="1.8">
                <path d="M4 9h16M4 15h16M10 3L8 21M16 3l-2 18" stroke-linecap="round"/>
              </svg>
            </button>
            <div class="emoji-wrapper">
              <button class="toolbar-btn" title="表情" @click="showEmoji = !showEmoji">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="var(--color-yellow)" stroke-width="1.8">
                  <circle cx="12" cy="12" r="10"/>
                  <path d="M8 14s1.5 2 4 2 4-2 4-2" stroke-linecap="round"/>
                  <line x1="9" y1="9" x2="9.01" y2="9" stroke-width="2.5" stroke-linecap="round"/>
                  <line x1="15" y1="9" x2="15.01" y2="9" stroke-width="2.5" stroke-linecap="round"/>
                </svg>
              </button>
              <EmojiPicker :visible="showEmoji" @select="insertEmoji" />
            </div>
            <span class="char-count" :class="{ 'near-limit': content.length > 450 }">
              {{ content.length }}/500
            </span>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn-publish" :disabled="!canPublish" @click="handlePublish">
            <span v-if="!publishing">发布</span>
            <span v-else class="loading-dots">
              <i></i><i></i><i></i>
            </span>
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import EmojiPicker from './EmojiPicker.vue'
import { useToast } from '../utils/toast'

const toast = useToast()

const props = defineProps({
  visible: { type: Boolean, default: false }
})

const emit = defineEmits(['close', 'publish'])

const content = ref('')
const images = ref([])
const publishing = ref(false)
const textareaRef = ref(null)
const showEmoji = ref(false)

const canPublish = computed(() => {
  return content.value.trim().length > 0 && !publishing.value
})

const close = () => {
  showEmoji.value = false
  emit('close')
}

const insertEmoji = (emoji) => {
  content.value += emoji
  showEmoji.value = false
}

const autoResize = () => {
  const el = textareaRef.value
  if (el) {
    el.style.height = 'auto'
    el.style.height = el.scrollHeight + 'px'
  }
}

const handleImageSelect = (e) => {
  const files = Array.from(e.target.files)
  if (images.value.length + files.length > 9) {
    toast.error('最多上传9张图片')
    return
  }
  const oversized = files.find(f => f.size > 10 * 1024 * 1024)
  if (oversized) {
    toast.error('单张图片不能超过10MB')
    return
  }
  files.forEach(file => {
    const url = URL.createObjectURL(file)
    images.value.push({ file, url })
  })
}

const removeImage = (index) => {
  URL.revokeObjectURL(images.value[index].url)
  images.value.splice(index, 1)
}

const insertTopic = () => {
  content.value += '#话题名#'
  nextTick(() => {
    const el = textareaRef.value
    if (el) {
      const pos = content.value.length - 1
      el.focus()
      el.setSelectionRange(pos - 3, pos)
    }
  })
}

const handlePublish = async () => {
  if (!canPublish.value) return
  publishing.value = true
  try {
    await emit('publish', {
      content: content.value,
      images: images.value.map(img => img.file)
    })
    content.value = ''
    images.value = []
    close()
  } finally {
    publishing.value = false
  }
}
</script>

<style>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(45, 45, 45, 0.4);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.publish-modal {
  background: var(--color-card);
  border-radius: var(--radius-lg);
  width: 90%;
  max-width: 520px;
  box-shadow: 0 20px 60px rgba(242, 167, 176, 0.15);
  animation: slideUp 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes slideUp {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 24px;
  border-bottom: 1px solid var(--color-border);
}

.modal-header h3 {
  font-size: 1.05rem;
  font-weight: 600;
}

.modal-close {
  padding: 4px;
  border-radius: 50%;
  color: var(--color-text-light);
  background: transparent;
}

.modal-close:hover {
  color: var(--color-primary);
  background: var(--color-hover);
}

.modal-body {
  padding: 20px 24px;
}

.publish-textarea {
  width: 100%;
  min-height: 120px;
  max-height: 300px;
  resize: none;
  font-size: 0.95rem;
  line-height: 1.7;
  color: var(--color-text);
}

.publish-textarea::placeholder {
  color: var(--color-text-light);
}

.image-preview-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  margin-top: 12px;
}

.preview-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: var(--radius-sm);
  overflow: hidden;
}

.preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-img {
  position: absolute;
  top: 4px;
  right: 4px;
  background: transparent;
  padding: 2px;
  border-radius: 50%;
}

.publish-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid var(--color-border);
}

.toolbar-btn {
  padding: 6px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  display: flex;
  align-items: center;
  background: transparent;
}

.toolbar-btn:hover {
  background: var(--color-hover);
}

.emoji-wrapper {
  position: relative;
}

.char-count {
  margin-left: auto;
  font-size: 0.8rem;
  color: var(--color-text-light);
  font-family: var(--font-mono);
}

.char-count.near-limit {
  color: var(--color-primary);
}

.modal-footer {
  padding: 16px 24px;
  border-top: 1px solid var(--color-border);
  display: flex;
  justify-content: flex-end;
}

.btn-publish {
  padding: 10px 32px;
  background: var(--color-primary);
  color: white;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
}

.btn-publish:hover:not(:disabled) {
  background: #e8939d;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(242, 167, 176, 0.3);
}

.btn-publish:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.loading-dots {
  display: flex;
  gap: 4px;
  align-items: center;
}

.loading-dots i {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: white;
  animation: dotBounce 0.6s infinite alternate;
}

.loading-dots i:nth-child(2) { animation-delay: 0.2s; }
.loading-dots i:nth-child(3) { animation-delay: 0.4s; }

@keyframes dotBounce {
  from { transform: translateY(0); }
  to { transform: translateY(-4px); }
}
</style>
