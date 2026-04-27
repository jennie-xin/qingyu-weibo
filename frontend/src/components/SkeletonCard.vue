<template>
  <div class="skeleton-card">
    <div class="skeleton-header">
      <div class="skeleton-avatar skeleton-pulse"></div>
      <div class="skeleton-meta">
        <div class="skeleton-line w-24 skeleton-pulse"></div>
        <div class="skeleton-line w-16 skeleton-pulse"></div>
      </div>
    </div>
    <div class="skeleton-body">
      <div class="skeleton-line w-full skeleton-pulse"></div>
      <div class="skeleton-line w-75 skeleton-pulse"></div>
      <div class="skeleton-line w-50 skeleton-pulse"></div>
    </div>
    <div class="skeleton-footer">
      <div class="skeleton-line w-16 skeleton-pulse"></div>
      <div class="skeleton-line w-16 skeleton-pulse"></div>
    </div>
  </div>
</template>

<script setup>
</script>

<style>
.skeleton-card {
  background: var(--color-card);
  border-radius: var(--radius-md);
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: var(--shadow-card);
}

.skeleton-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.skeleton-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: var(--color-border);
}

.skeleton-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.skeleton-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.skeleton-footer {
  display: flex;
  gap: 24px;
  padding-top: 12px;
  border-top: 1px solid var(--color-border);
}

.skeleton-line {
  height: 12px;
  border-radius: 6px;
  background: var(--color-border);
}

.w-full { width: 100%; }
.w-75 { width: 75%; }
.w-50 { width: 50%; }
.w-24 { width: 96px; }
.w-16 { width: 64px; }

.skeleton-pulse {
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.4; }
  50% { opacity: 0.8; }
}
</style>
