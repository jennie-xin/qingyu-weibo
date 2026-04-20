<template>
  <div class="login-page">
    <div class="login-decor">
      <div class="decor-circle c1"></div>
      <div class="decor-circle c2"></div>
      <div class="decor-circle c3"></div>
      <h2 class="decor-title">轻语</h2>
      <p class="decor-subtitle">记录你的碎碎念</p>
    </div>
    <div class="login-form-wrapper">
      <h3 class="form-title">登录</h3>
      <form class="login-form" @submit.prevent="handleLogin">
        <div class="form-group">
          <label>用户名</label>
          <input type="text" v-model="form.username" placeholder="请输入用户名" />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input type="password" v-model="form.password" placeholder="请输入密码" />
        </div>
        <p v-if="errorMsg" class="form-error">{{ errorMsg }}</p>
        <button type="submit" class="btn-primary">登录</button>
        <p class="form-footer">
          还没有账号？<router-link to="/register">去注册</router-link>
        </p>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const errorMsg = ref('')

const form = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  errorMsg.value = ''
  if (!form.username || !form.password) {
    errorMsg.value = '请填写用户名和密码'
    return
  }
  try {
    await userStore.login({ username: form.username, password: form.password })
    router.push('/')
  } catch (e) {
    errorMsg.value = e.message || '登录失败'
  }
}
</script>

<style>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: stretch;
}

.login-decor {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary), var(--color-purple));
  position: relative;
  overflow: hidden;
}

.decor-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.2;
}

.c1 {
  width: 200px;
  height: 200px;
  background: var(--color-yellow);
  top: 10%;
  left: 10%;
}

.c2 {
  width: 150px;
  height: 150px;
  background: var(--color-blue);
  bottom: 20%;
  right: 15%;
}

.c3 {
  width: 100px;
  height: 100px;
  background: var(--color-green);
  top: 50%;
  left: 60%;
}

.decor-title {
  font-family: var(--font-mono);
  font-size: 2.5rem;
  color: white;
  z-index: 1;
}

.decor-subtitle {
  color: rgba(255, 255, 255, 0.8);
  margin-top: 8px;
  z-index: 1;
}

.login-form-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: var(--color-card);
}

.form-title {
  font-size: 1.5rem;
  margin-bottom: 32px;
  color: var(--color-text);
}

.login-form {
  width: 100%;
  max-width: 320px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-size: 0.85rem;
  color: var(--color-text-light);
  margin-bottom: 6px;
}

.form-group input {
  width: 100%;
  padding: 12px 16px;
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: 0.95rem;
  transition: var(--transition);
}

.form-group input:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(242, 167, 176, 0.1);
}

.btn-primary {
  width: 100%;
  padding: 12px;
  background: var(--color-primary);
  color: white;
  border-radius: var(--radius-sm);
  font-size: 1rem;
  font-weight: 600;
  margin-top: 8px;
}

.btn-primary:hover {
  background: #e8939d;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(242, 167, 176, 0.3);
}

.form-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 0.85rem;
  color: var(--color-text-light);
}

.form-error {
  color: #e74c3c;
  font-size: 0.85rem;
  margin-bottom: 8px;
  padding: 8px 12px;
  background: #ffeaea;
  border-radius: var(--radius-sm);
}
</style>
