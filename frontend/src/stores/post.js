import { defineStore } from 'pinia'
import { ref } from 'vue'
import { postApi } from '../api'

export const usePostStore = defineStore('post', () => {
  const posts = ref([])
  const loading = ref(false)
  const currentPage = ref(1)
  const hasMore = ref(true)

  const parseImages = (post) => {
    if (post.images && typeof post.images === 'string') {
      try {
        post.images = JSON.parse(post.images)
      } catch (e) {
        post.images = null
      }
    }
    return post
  }

  const fetchPosts = async (page = 1) => {
    loading.value = true
    try {
      const res = await postApi.getList({ page, size: 10 })
      const records = res.data.records.map(parseImages)
      if (page === 1) {
        posts.value = records
      } else {
        posts.value.push(...records)
      }
      currentPage.value = page
      hasMore.value = res.data.records.length === 10
    } finally {
      loading.value = false
    }
  }

  const addPost = (post) => {
    posts.value.unshift(parseImages(post))
  }

  const updatePost = (id, updatedPost) => {
    const index = posts.value.findIndex(p => p.id === id)
    if (index !== -1) {
      posts.value[index] = parseImages({ ...posts.value[index], ...updatedPost })
    }
  }

  const removePost = (id) => {
    posts.value = posts.value.filter(p => p.id !== id)
  }

  const toggleLike = (postId, liked, likeCount) => {
    const post = posts.value.find(p => p.id === postId)
    if (post) {
      post.liked = liked
      post.likeCount = likeCount
    }
  }

  return {
    posts,
    loading,
    currentPage,
    hasMore,
    fetchPosts,
    addPost,
    updatePost,
    removePost,
    toggleLike
  }
})
