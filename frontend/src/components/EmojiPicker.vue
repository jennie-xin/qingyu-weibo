<template>
  <div class="emoji-picker" v-if="visible">
    <div class="emoji-grid">
      <button
        v-for="emoji in emojis"
        :key="emoji"
        class="emoji-item"
        @click="$emit('select', emoji)"
      >
        {{ emoji }}
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  visible: { type: Boolean, default: false }
})

defineEmits(['select'])

const emojis = [
  '😊', '😂', '🥰', '😍', '🤔', '😅', '😭', '🥺',
  '✨', '🎉', '💕', '❤️', '🔥', '👍', '👏', '🙌',
  '😎', '🤗', '😴', '🌸', '🌈', '☀️', '🌙', '⭐',
  '🍰', '🧋', '🎵', '📷', '💡', '🎨', '🌿', '🦋'
]
</script>

<style>
.emoji-picker {
  position: absolute;
  bottom: 48px;
  left: 0;
  background: var(--color-card);
  border-radius: var(--radius-md);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  padding: 12px;
  z-index: 10;
  animation: fadeIn 0.15s ease;
}

.emoji-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 4px;
}

.emoji-item {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-sm);
  font-size: 1.2rem;
  background: transparent;
  transition: var(--transition);
}

.emoji-item:hover {
  background: var(--color-hover);
  transform: scale(1.2);
}
</style>
