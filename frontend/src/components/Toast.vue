<template>
  <Teleport to="body">
    <div class="toast-container">
      <TransitionGroup name="toast">
        <div
          v-for="toast in toasts"
          :key="toast.id"
          class="toast-item"
          :class="toast.type"
        >
          <span class="toast-icon">
            <svg v-if="toast.type === 'success'" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
              <path d="M20 6L9 17l-5-5"/>
            </svg>
            <svg v-else-if="toast.type === 'error'" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
              <circle cx="12" cy="12" r="10" stroke-width="1.8"/>
              <path d="M15 9l-6 6M9 9l6 6"/>
            </svg>
            <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
              <circle cx="12" cy="12" r="10" stroke-width="1.8"/>
              <path d="M12 16v-4M12 8h.01"/>
            </svg>
          </span>
          <span class="toast-text">{{ toast.message }}</span>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<script setup>
import { useToast } from '../utils/toast'

const { toasts } = useToast()
</script>

<style>
.toast-container {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  pointer-events: none;
}

.toast-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 20px;
  background: var(--color-card);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  font-size: 0.85rem;
  pointer-events: auto;
}

.toast-item.success {
  border-left: 3px solid var(--color-green);
}

.toast-item.success .toast-icon {
  color: var(--color-green);
}

.toast-item.error {
  border-left: 3px solid #e74c3c;
}

.toast-item.error .toast-icon {
  color: #e74c3c;
}

.toast-item.info {
  border-left: 3px solid var(--color-blue);
}

.toast-item.info .toast-icon {
  color: var(--color-blue);
}

.toast-icon {
  display: flex;
  align-items: center;
}

.toast-text {
  color: var(--color-text);
}

.toast-enter-active {
  animation: toastIn 0.3s ease;
}

.toast-leave-active {
  animation: toastOut 0.3s ease;
}

@keyframes toastIn {
  from { transform: translateY(-20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

@keyframes toastOut {
  from { transform: translateY(0); opacity: 1; }
  to { transform: translateY(-20px); opacity: 0; }
}
</style>
