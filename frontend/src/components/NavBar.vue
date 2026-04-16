<template>
  <nav class="navbar">
    <div class="navbar-inner">
      <router-link to="/" class="navbar-brand">
        <span class="brand-dot"></span>
        轻语
      </router-link>
      <div class="navbar-actions">
        <router-link to="/notifications" class="nav-icon" title="通知">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
            <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
          </svg>
        </router-link>
        <router-link to="/user/1" class="nav-avatar">
          <div class="avatar-placeholder"></div>
        </router-link>
      </div>
    </div>
  </nav>
</template>

<script setup>
</script>

<style>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(253, 246, 240, 0.85);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--color-border);
  transition: var(--transition);
}

.navbar-inner {
  max-width: 800px;
  margin: 0 auto;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.navbar-brand {
  font-family: var(--font-mono);
  font-size: 1.2rem;
  font-weight: 700;
  color: var(--color-text);
  display: flex;
  align-items: center;
  gap: 8px;
}

.navbar-brand:hover {
  color: var(--color-primary);
}

.brand-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary), var(--color-purple));
}

.navbar-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.nav-icon {
  color: var(--color-text-light);
  display: flex;
  align-items: center;
  padding: 6px;
  border-radius: var(--radius-sm);
}

.nav-icon:hover {
  color: var(--color-primary);
  background: var(--color-hover);
}

.avatar-placeholder {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-blue), var(--color-green));
}
</style>
