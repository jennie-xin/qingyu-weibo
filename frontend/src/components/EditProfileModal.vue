<template>
  <Teleport to="body">
    <div v-if="visible" class="modal-overlay" @click.self="$emit('close')">
      <div class="edit-profile-modal">
        <div class="modal-header">
          <h3>编辑资料</h3>
          <button class="modal-close" @click="$emit('close')">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
              <circle cx="12" cy="12" r="10" stroke-width="1.5" opacity="0.3"/>
              <path d="M15 9l-6 6M9 9l6 6"/>
            </svg>
          </button>
        </div>

        <div class="modal-body">
          <div class="edit-visual-section">
            <div class="edit-cover-area">
              <div v-if="form.cover" class="edit-cover-img">
                <img :src="form.cover" alt="" :style="{ objectPosition: `center ${coverPosition}%` }" />
              </div>
              <div v-else class="edit-cover-default" :style="coverBgStyle">
                <span class="cover-initial">{{ initial }}</span>
              </div>
              <label class="edit-cover-btn">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M23 19a2 2 0 01-2 2H3a2 2 0 01-2-2V8a2 2 0 012-2h4l2-3h6l2 3h4a2 2 0 012 2z"/>
                  <circle cx="12" cy="13" r="4"/>
                </svg>
                <input type="file" accept="image/*" hidden @change="handleCoverChange" />
              </label>
            </div>
            <div class="edit-avatar-area">
              <div class="edit-avatar" :style="avatarStyle">
                <img v-if="form.avatar" :src="form.avatar" alt="" />
                <span v-else class="avatar-letter-lg">{{ initial }}</span>
                <label class="edit-avatar-btn">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M23 19a2 2 0 01-2 2H3a2 2 0 01-2-2V8a2 2 0 012-2h4l2-3h6l2 3h4a2 2 0 012 2z"/>
                    <circle cx="12" cy="13" r="4"/>
                  </svg>
                  <input type="file" accept="image/*" hidden @change="handleAvatarChange" />
                </label>
              </div>
            </div>
          </div>

          <div v-if="form.cover" class="cover-position-control">
            <span class="position-label">封面位置</span>
            <input type="range" min="0" max="100" v-model="coverPosition" class="position-slider" />
          </div>

          <div class="edit-form">
            <div class="form-group">
              <label>昵称</label>
              <input type="text" v-model="form.nickname" placeholder="你的昵称" maxlength="32" />
            </div>
            <div class="form-group">
              <label>个人简介</label>
              <textarea v-model="form.bio" placeholder="介绍一下自己吧" rows="4" maxlength="200"></textarea>
              <span class="bio-count">{{ (form.bio || '').length }}/200</span>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn-cancel" @click="$emit('close')">取消</button>
          <button class="btn-save" @click="handleSave" :disabled="saving">
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { reactive, ref, computed, watch } from 'vue'
import { userApi, uploadApi } from '../api'
import { useUserStore } from '../stores/user'
import { useToast } from '../utils/toast'

const props = defineProps({
  visible: { type: Boolean, default: false },
  user: { type: Object, default: () => ({}) }
})

const emit = defineEmits(['close', 'updated'])
const userStore = useUserStore()
const toast = useToast()
const saving = ref(false)
const coverPosition = ref(50)

const form = reactive({
  nickname: '',
  bio: '',
  avatar: '',
  cover: ''
})

watch(() => props.visible, (val) => {
  if (val && props.user) {
    form.nickname = props.user.nickname || ''
    form.bio = props.user.bio || ''
    form.avatar = props.user.avatar || ''
    const raw = props.user.cover || ''
    const posMatch = raw.match(/#pos=(\d+)/)
    form.cover = raw.split('#pos=')[0]
    coverPosition.value = posMatch ? parseInt(posMatch[1]) : 50
  }
})

const initial = computed(() => {
  return (form.nickname || 'U').charAt(0).toUpperCase()
})

const coverBgStyle = computed(() => {
  const colors = ['#F2A7B0', '#A8D8EA', '#C3B1E1', '#B5EAD7', '#FFEAA7']
  const index = (props.user?.id || 0) % colors.length
  return { background: `linear-gradient(135deg, ${colors[index]}, ${colors[(index + 2) % colors.length]})` }
})

const avatarStyle = computed(() => {
  if (form.avatar) return {}
  const colors = ['#F2A7B0', '#A8D8EA', '#C3B1E1', '#B5EAD7', '#FFEAA7']
  const index = (props.user?.id || 0) % colors.length
  return { background: colors[index] }
})

const handleAvatarChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  try {
    const res = await uploadApi.image(file)
    form.avatar = res.data
  } catch (err) {
    toast.error('头像上传失败')
  }
}

const handleCoverChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  try {
    const res = await uploadApi.image(file)
    form.cover = res.data
    coverPosition.value = 50
  } catch (err) {
    toast.error('封面上传失败')
  }
}

const handleSave = async () => {
  if (!form.nickname.trim()) {
    toast.error('昵称不能为空')
    return
  }
  saving.value = true
  try {
    const coverVal = form.cover ? `${form.cover}#pos=${coverPosition.value}` : ''
    const res = await userApi.updateProfile({
      nickname: form.nickname,
      bio: form.bio,
      avatar: form.avatar,
      cover: coverVal
    })
    userStore.userInfo = res.data
    toast.success('资料更新成功')
    emit('updated')
    emit('close')
  } catch (err) {
    toast.error(err.message || '保存失败')
  } finally {
    saving.value = false
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
}

.edit-profile-modal {
  background: var(--color-card);
  border-radius: var(--radius-lg);
  width: 92%;
  max-width: 520px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(242, 167, 176, 0.15);
  animation: slideUp 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px 12px;
}

.modal-header h3 {
  font-size: 1.15rem;
  font-weight: 600;
}

.modal-close {
  background: none;
  border: none;
  color: var(--color-text-light);
  cursor: pointer;
  padding: 4px;
}

.modal-body {
  padding: 0 24px 16px;
}

.edit-visual-section {
  position: relative;
  margin-bottom: 50px;
}

.edit-cover-area {
  position: relative;
  width: 100%;
  height: 140px;
  border-radius: var(--radius-md);
  overflow: hidden;
}

.edit-cover-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.edit-cover-img {
  width: 100%;
  height: 100%;
}

.edit-cover-default {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-initial {
  font-size: 3rem;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.6);
  font-family: var(--font-mono);
}

.edit-cover-btn {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: white;
  transition: var(--transition);
}

.edit-cover-btn:hover {
  background: rgba(0, 0, 0, 0.65);
}

.edit-avatar-area {
  position: absolute;
  bottom: -36px;
  left: 20px;
}

.edit-avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 4px solid var(--color-card);
  position: relative;
}

.edit-avatar img {
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

.edit-avatar-btn {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: white;
  opacity: 0;
  transition: var(--transition);
}

.edit-avatar:hover .edit-avatar-btn {
  opacity: 1;
}

.cover-position-control {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.position-label {
  font-size: 0.8rem;
  color: var(--color-text-light);
  white-space: nowrap;
}

.position-slider {
  flex: 1;
  height: 4px;
  -webkit-appearance: none;
  appearance: none;
  background: var(--color-border);
  border-radius: 2px;
  outline: none;
}

.position-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: var(--color-primary);
  cursor: pointer;
}

.edit-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.edit-form .form-group label {
  display: block;
  font-size: 0.85rem;
  font-weight: 500;
  color: var(--color-text);
  margin-bottom: 6px;
}

.edit-form .form-group input,
.edit-form .form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: 0.95rem;
  background: var(--color-bg);
  color: var(--color-text);
  transition: var(--transition);
}

.edit-form .form-group input:focus,
.edit-form .form-group textarea:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(242, 167, 176, 0.1);
  outline: none;
}

.edit-form .form-group textarea {
  resize: none;
}

.bio-count {
  display: block;
  text-align: right;
  font-size: 0.75rem;
  color: var(--color-text-light);
  margin-top: 4px;
  font-family: var(--font-mono);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 0 24px 20px;
}

.btn-cancel {
  padding: 9px 22px;
  border-radius: 18px;
  background: transparent;
  border: 1.5px solid var(--color-border);
  color: var(--color-text-light);
  font-size: 0.85rem;
  cursor: pointer;
}

.btn-cancel:hover {
  border-color: var(--color-text-light);
}

.btn-save {
  padding: 9px 28px;
  border-radius: 18px;
  background: var(--color-primary);
  color: white;
  font-size: 0.85rem;
  font-weight: 600;
  border: none;
  cursor: pointer;
}

.btn-save:hover:not(:disabled) {
  background: #e8939d;
}

.btn-save:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
