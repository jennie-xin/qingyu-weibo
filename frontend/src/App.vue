<template>
  <div class="app">
    <NavBar v-if="showNav" />
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from './components/NavBar.vue'

const route = useRoute()

const showNav = computed(() => {
  return !['Login', 'Register'].includes(route.name)
})
</script>

<style>
.app {
  min-height: 100vh;
  background: var(--color-bg);
}

.main-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 80px 16px 32px;
}
</style>
