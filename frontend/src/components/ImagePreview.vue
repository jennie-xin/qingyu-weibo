<template>
  <Teleport to="body">
    <div v-if="visible" class="image-preview-overlay" @click="$emit('close')">
      <button class="preview-close" @click="$emit('close')">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round">
          <path d="M18 6L6 18M6 6l12 12"/>
        </svg>
      </button>
      <div class="preview-nav" v-if="images.length > 1">
        <button class="nav-arrow prev" @click.stop="prev" :disabled="currentIndex === 0">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round">
            <path d="M15 18l-6-6 6-6"/>
          </svg>
        </button>
        <button class="nav-arrow next" @click.stop="next" :disabled="currentIndex === images.length - 1">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round">
            <path d="M9 18l6-6-6-6"/>
          </svg>
        </button>
      </div>
      <img :src="images[currentIndex]" alt="" class="preview-image" @click.stop />
      <div class="preview-counter" v-if="images.length > 1">
        {{ currentIndex + 1 }} / {{ images.length }}
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  visible: { type: Boolean, default: false },
  images: { type: Array, default: () => [] },
  startIndex: { type: Number, default: 0 }
})

defineEmits(['close'])

const currentIndex = ref(0)

watch(() => props.startIndex, (val) => {
  currentIndex.value = val
})

watch(() => props.visible, (val) => {
  if (val) currentIndex.value = props.startIndex
})

const prev = () => {
  if (currentIndex.value > 0) currentIndex.value--
}

const next = () => {
  if (currentIndex.value < props.images.length - 1) currentIndex.value++
}
</script>

<style>
.image-preview-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  animation: fadeIn 0.2s ease;
}

.preview-close {
  position: absolute;
  top: 20px;
  right: 20px;
  padding: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.preview-close:hover {
  background: rgba(255, 255, 255, 0.2);
}

.preview-image {
  max-width: 90vw;
  max-height: 85vh;
  object-fit: contain;
  border-radius: var(--radius-sm);
}

.preview-nav {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  padding: 0 16px;
  transform: translateY(-50%);
  pointer-events: none;
}

.nav-arrow {
  padding: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  pointer-events: auto;
}

.nav-arrow:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.2);
}

.nav-arrow:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.preview-counter {
  position: absolute;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.85rem;
  font-family: var(--font-mono);
}
</style>
