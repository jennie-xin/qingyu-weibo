import { defineStore } from 'pinia'
import { ref } from 'vue'
import { postApi } from '../api'

export const usePostStore = defineStore('post', () => {
  const posts = ref([])
  const loading = ref(false)
  const currentPage = ref(1)
  const hasMore = ref(true)

  const fetchPosts = async (page = 1) => {
    loading.value = true
    try {
      const res = await postApi.getList({ page, size: 10 })
      if (page === 1) {
        posts.value = res.data.records
      } else {
        posts.value.push(...res.data.records)
      }
      currentPage.value = page
      hasMore.value = res.data.records.length === 10
    } finally {
      loading.value = false
    }
  }

  const addPost = (post) => {
    posts.value.unshift(post)
  }

  const removePost = (id) => {
    posts.value = posts.value.filter(p => p.id !== id)
  }

  const toggleLike = (postId, liked) => {
    const post = posts.value.find(p => p.id === postId)
    if (post) {
      post.liked = liked
      post.likeCount += liked ? 1 : -1
    }
  }

  return {
    posts,
    loading,
    currentPage,
    hasMore,
    fetchPosts,
    addPost,
    removePost,
    toggleLike
  }
})
