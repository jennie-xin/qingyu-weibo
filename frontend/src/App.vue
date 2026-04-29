<template>
  <div class="app">
    <NavBar v-if="showNav" />
    <main class="main-content" :class="{ 'full-width': isFullWidth }">
      <router-view />
    </main>
    <AppFooter v-if="showNav" />
    <Toast />
    <BackToTop />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from './components/NavBar.vue'
import AppFooter from './components/AppFooter.vue'
import Toast from './components/Toast.vue'
import BackToTop from './components/BackToTop.vue'

const route = useRoute()

const showNav = computed(() => {
  return !['Login', 'Register'].includes(route.name)
})

const isFullWidth = computed(() => {
  return ['Login', 'Register'].includes(route.name)
})
</script>

<style>
.app {
  min-height: 100vh;
  background: var(--color-bg);
}

.main-content {
  max-width: 1080px;
  margin: 0 auto;
  padding: 80px 16px 32px;
}

.main-content.full-width {
  max-width: none;
  padding: 0;
}
</style>
