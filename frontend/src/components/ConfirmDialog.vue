<template>
  <Teleport to="body">
    <div v-if="visible" class="confirm-overlay" @click.self="handleCancel">
      <div class="confirm-dialog">
        <div class="confirm-icon">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="var(--color-primary)" stroke-width="1.5">
            <circle cx="12" cy="12" r="10"/>
            <path d="M12 8v4M12 16h.01" stroke-linecap="round"/>
          </svg>
        </div>
        <p class="confirm-message">{{ message }}</p>
        <div class="confirm-actions">
          <button class="confirm-btn cancel" @click="handleCancel">取消</button>
          <button class="confirm-btn danger" @click="handleConfirm">确认删除</button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
defineProps({
  visible: { type: Boolean, default: false },
  message: { type: String, default: '确认删除吗？' }
})

const emit = defineEmits(['confirm', 'cancel'])

const handleConfirm = () => emit('confirm')
const handleCancel = () => emit('cancel')
</script>

<style>
.confirm-overlay {
  position: fixed;
  inset: 0;
  background: rgba(45, 45, 45, 0.4);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1100;
  animation: fadeIn 0.2s ease;
}

.confirm-dialog {
  background: var(--color-card);
  border-radius: var(--radius-lg);
  padding: 32px 28px 24px;
  width: 90%;
  max-width: 320px;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  animation: slideUp 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.confirm-icon {
  margin-bottom: 16px;
}

.confirm-message {
  font-size: 0.95rem;
  color: var(--color-text);
  margin-bottom: 24px;
  font-weight: 500;
}

.confirm-actions {
  display: flex;
  gap: 12px;
}

.confirm-btn {
  flex: 1;
  padding: 10px 0;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
  transition: var(--transition);
}

.confirm-btn.cancel {
  background: var(--color-bg);
  color: var(--color-text-light);
  border: 1px solid var(--color-border);
}

.confirm-btn.cancel:hover {
  background: var(--color-hover);
}

.confirm-btn.danger {
  background: #e74c3c;
  color: white;
}

.confirm-btn.danger:hover {
  background: #c0392b;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(231, 76, 60, 0.3);
}
</style>
