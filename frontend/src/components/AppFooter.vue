<template>
  <footer class="app-footer">
    <div class="footer-inner">
      <div class="footer-brand">
        <span class="brand-dot"></span>
        <span>轻语</span>
      </div>
      <p class="footer-text">用文字记录生活的温度</p>
      <p class="footer-copy">Made with care &middot; 2026</p>
    </div>
  </footer>
</template>

<script setup>
</script>

<style>
.app-footer {
  margin-top: 60px;
  padding: 32px 16px;
  border-top: 1px solid var(--color-border);
  text-align: center;
}

.footer-inner {
  max-width: 1240px;
  margin: 0 auto;
}

.footer-brand {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-family: var(--font-mono);
  font-weight: 700;
  font-size: 1rem;
  color: var(--color-text);
  margin-bottom: 6px;
}

.footer-brand .brand-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary), var(--color-purple));
}

.footer-text {
  font-size: 0.8rem;
  color: var(--color-text-light);
  margin-bottom: 4px;
}

.footer-copy {
  font-size: 0.75rem;
  color: var(--color-text-light);
  opacity: 0.6;
}
</style>
