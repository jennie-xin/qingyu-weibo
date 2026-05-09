<template>
  <div class="not-found">
    <div class="not-found-content">
      <div class="not-found-code">404</div>
      <p class="not-found-text">页面走丢了...</p>
      <router-link to="/" class="btn-go-home">回到首页</router-link>
    </div>
  </div>
</template>

<script setup>
</script>

<style>
.not-found {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
}

.not-found-content {
  text-align: center;
}

.not-found-code {
  font-family: var(--font-mono);
  font-size: 5rem;
  font-weight: 700;
  background: linear-gradient(135deg, var(--color-primary), var(--color-purple));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1;
  margin-bottom: 12px;
}

.not-found-text {
  font-size: 1.1rem;
  color: var(--color-text-light);
  margin-bottom: 24px;
}

.btn-go-home {
  display: inline-block;
  padding: 10px 28px;
  background: var(--color-primary);
  color: white;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
}

.btn-go-home:hover {
  background: #e8939d;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(242, 167, 176, 0.3);
}
</style>
