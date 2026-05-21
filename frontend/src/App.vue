<template>
  <div class="app">
    <NavBar v-if="showNav" @publish="openPublish" />
    <LoginPromptBar v-if="showNav" />
    <main class="main-content" :class="{ 'full-width': isFullWidth }">
      <router-view v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" ref="currentView" />
        </transition>
      </router-view>
    </main>
    <AppFooter v-if="showNav" />
    <Toast />
    <BackToTop />

    <PublishModal
      :visible="showPublish"
      @close="showPublish = false; editingPost = null"
      :on-publish="handlePublish"
      :edit-post="editingPost"
    />
  </div>
</template>

<script setup>
import { ref, computed, provide } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from './stores/user'
import { usePostStore } from './stores/post'
import { postApi, uploadApi } from './api'
import NavBar from './components/NavBar.vue'
import AppFooter from './components/AppFooter.vue'
import Toast from './components/Toast.vue'
import BackToTop from './components/BackToTop.vue'
import LoginPromptBar from './components/LoginPromptBar.vue'
import PublishModal from './components/PublishModal.vue'

const route = useRoute()
const userStore = useUserStore()
const postStore = usePostStore()

const showNav = computed(() => {
  return !['Login', 'Register'].includes(route.name)
})

const isFullWidth = computed(() => {
  return ['Login', 'Register'].includes(route.name)
})

const showPublish = ref(false)
const editingPost = ref(null)

const openPublish = () => {
  editingPost.value = null
  showPublish.value = true
}

const openEdit = (post) => {
  editingPost.value = post
  showPublish.value = true
}

const handlePublish = async ({ content, images, existingImages, isEdit, postId }) => {
  let imageUrls = existingImages || []
  if (images.length) {
    for (const file of images) {
      const res = await uploadApi.image(file)
      imageUrls.push(res.data)
    }
  }

  if (isEdit) {
    const res = await postApi.update(postId, {
      content,
      images: imageUrls.length ? JSON.stringify(imageUrls) : null
    })
    postStore.updatePost(postId, res.data)
    editingPost.value = null
  } else {
    const res = await postApi.create({
      content,
      images: imageUrls.length ? JSON.stringify(imageUrls) : null
    })
    postStore.addPost(res.data)
  }
}

provide('openPublish', openPublish)
provide('openEdit', openEdit)
</script>

<style>
.app {
  min-height: 100vh;
  background: var(--color-bg);
}

.main-content {
  max-width: 1240px;
  margin: 0 auto;
  padding: 80px 16px 32px;
}

.main-content.full-width {
  max-width: none;
  padding: 0;
}

.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.2s ease;
}

.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
}
</style>
