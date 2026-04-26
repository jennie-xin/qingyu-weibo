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
          <div class="edit-avatar-section">
            <div class="edit-avatar" :style="avatarStyle">
              <img v-if="form.avatar" :src="form.avatar" alt="" />
              <span v-else class="avatar-letter-lg">{{ initial }}</span>
            </div>
            <label class="btn-change-avatar">
              更换头像
              <input type="file" accept="image/*" hidden @change="handleAvatarChange" />
            </label>
          </div>

          <div class="form-group">
            <label>昵称</label>
            <input type="text" v-model="form.nickname" placeholder="你的昵称" maxlength="32" />
          </div>

          <div class="form-group">
            <label>个人简介</label>
            <textarea v-model="form.bio" placeholder="介绍一下自己吧" rows="3" maxlength="200"></textarea>
            <span class="bio-count">{{ (form.bio || '').length }}/200</span>
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

const form = reactive({
  nickname: '',
  bio: '',
  avatar: ''
})

watch(() => props.visible, (val) => {
  if (val && props.user) {
    form.nickname = props.user.nickname || ''
    form.bio = props.user.bio || ''
    form.avatar = props.user.avatar || ''
  }
})

const initial = computed(() => {
  return (form.nickname || 'U').charAt(0).toUpperCase()
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

const handleSave = async () => {
  if (!form.nickname.trim()) {
    toast.error('昵称不能为空')
    return
  }
  saving.value = true
  try {
    const res = await userApi.updateProfile({
      nickname: form.nickname,
      bio: form.bio,
      avatar: form.avatar
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
.edit-profile-modal {
  background: var(--color-card);
  border-radius: var(--radius-lg);
  width: 90%;
  max-width: 420px;
  box-shadow: 0 20px 60px rgba(242, 167, 176, 0.15);
  animation: slideUp 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.edit-avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.edit-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.edit-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.btn-change-avatar {
  padding: 6px 16px;
  border: 1.5px solid var(--color-border);
  border-radius: 16px;
  font-size: 0.8rem;
  color: var(--color-text-light);
  cursor: pointer;
  transition: var(--transition);
}

.btn-change-avatar:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: 0.95rem;
  resize: none;
  transition: var(--transition);
}

.form-group textarea:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(242, 167, 176, 0.1);
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
}

.btn-cancel {
  padding: 8px 20px;
  border-radius: 16px;
  background: transparent;
  border: 1.5px solid var(--color-border);
  color: var(--color-text-light);
  font-size: 0.85rem;
}

.btn-cancel:hover {
  border-color: var(--color-text-light);
}

.btn-save {
  padding: 8px 24px;
  border-radius: 16px;
  background: var(--color-primary);
  color: white;
  font-size: 0.85rem;
  font-weight: 600;
}

.btn-save:hover:not(:disabled) {
  background: #e8939d;
}

.btn-save:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
