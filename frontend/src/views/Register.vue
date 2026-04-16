<template>
  <div class="login-page">
    <div class="login-decor">
      <div class="decor-circle c1"></div>
      <div class="decor-circle c2"></div>
      <div class="decor-circle c3"></div>
      <h2 class="decor-title">轻语</h2>
      <p class="decor-subtitle">加入我们，分享你的故事</p>
    </div>
    <div class="login-form-wrapper">
      <h3 class="form-title">注册</h3>
      <form class="login-form" @submit.prevent="handleRegister">
        <div class="form-group">
          <label>用户名</label>
          <input type="text" v-model="form.username" placeholder="字母数字，4-16位" />
        </div>
        <div class="form-group">
          <label>昵称</label>
          <input type="text" v-model="form.nickname" placeholder="给自己取个名字" />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input type="password" v-model="form.password" placeholder="至少6位" />
        </div>
        <div class="form-group">
          <label>确认密码</label>
          <input type="password" v-model="form.confirmPassword" placeholder="再输一次" />
        </div>
        <button type="submit" class="btn-primary">注册</button>
        <p class="form-footer">
          已有账号？<router-link to="/login">去登录</router-link>
        </p>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const form = reactive({
  username: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})

const handleRegister = () => {
  if (form.password !== form.confirmPassword) {
    alert('两次密码不一致')
    return
  }
  console.log('register:', form)
}
</script>
