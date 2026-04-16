import api from './request'

export const authApi = {
  login(data) {
    return api.post('/auth/login', data)
  },
  register(data) {
    return api.post('/auth/register', data)
  },
  getProfile() {
    return api.get('/auth/profile')
  }
}

export const postApi = {
  getList(params) {
    return api.get('/posts', { params })
  },
  getDetail(id) {
    return api.get(`/posts/${id}`)
  },
  create(data) {
    return api.post('/posts', data)
  },
  remove(id) {
    return api.delete(`/posts/${id}`)
  }
}

export const commentApi = {
  getList(postId) {
    return api.get(`/posts/${postId}/comments`)
  },
  create(postId, data) {
    return api.post(`/posts/${postId}/comments`, data)
  }
}

export const likeApi = {
  toggle(postId) {
    return api.post(`/posts/${postId}/like`)
  }
}

export const uploadApi = {
  image(file) {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

export const userApi = {
  getInfo(id) {
    return api.get(`/users/${id}`)
  },
  updateProfile(data) {
    return api.put('/users/profile', data)
  }
}

export const topicApi = {
  getHot() {
    return api.get('/topics/hot')
  },
  getPosts(name) {
    return api.get(`/topics/${name}`)
  }
}

export const notificationApi = {
  getList() {
    return api.get('/notifications')
  },
  markRead(id) {
    return api.put(`/notifications/${id}/read`)
  }
}

export const adminApi = {
  getUsers(params) {
    return api.get('/admin/users', { params })
  },
  deletePost(id) {
    return api.delete(`/admin/posts/${id}`)
  },
  updateUserRole(id, role) {
    return api.put(`/admin/users/${id}/role`, { role })
  }
}
