<template>
  <div class="loading-spinner">
    <div class="macaron-dots">
      <span class="dot dot-1"></span>
      <span class="dot dot-2"></span>
      <span class="dot dot-3"></span>
    </div>
    <p v-if="text" class="loading-text">{{ text }}</p>
  </div>
</template>

<script setup>
defineProps({
  text: { type: String, default: '' }
})
</script>

<style>
.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 0;
}

.macaron-dots {
  display: flex;
  gap: 8px;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  animation: bounce 1.2s infinite ease-in-out;
}

.dot-1 {
  background: var(--color-primary);
  animation-delay: 0s;
}

.dot-2 {
  background: var(--color-blue);
  animation-delay: 0.2s;
}

.dot-3 {
  background: var(--color-purple);
  animation-delay: 0.4s;
}

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.4; }
  40% { transform: scale(1); opacity: 1; }
}

.loading-text {
  margin-top: 12px;
  font-size: 0.85rem;
  color: var(--color-text-light);
}
</style>
