import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截：自动带上 token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截：统一处理错误
api.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code && res.code !== 200) {
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    const msg = error.response?.data?.message || '网络开小差了'
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(new Error(msg))
  }
)

export default api
