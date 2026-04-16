import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '../api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'admin')

  const login = async (form) => {
    const res = await authApi.login(form)
    token.value = res.data.token
    userInfo.value = res.data.user
    localStorage.setItem('token', res.data.token)
    return res
  }

  const register = async (form) => {
    const res = await authApi.register(form)
    return res
  }

  const fetchProfile = async () => {
    if (!token.value) return
    const res = await authApi.getProfile()
    userInfo.value = res.data
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    login,
    register,
    fetchProfile,
    logout
  }
})
