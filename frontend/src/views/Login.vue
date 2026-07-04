<template>
  <div class="page login-page">
    <div class="login-box">
      <div class="dino-login">🦕</div>
      <h1>Dino 学习乐园</h1>
      <p class="subtitle">和 Dino 一起学数学、英语、古诗吧！</p>
      <div class="tab-bar">
        <button :class="{active: mode==='login'}" @click="mode='login'">登录</button>
        <button :class="{active: mode==='register'}" @click="mode='register'">注册</button>
      </div>
      <form @submit.prevent="submit" class="login-form">
        <div v-if="mode==='register'" class="field">
          <label>孩子名字</label>
          <input v-model="name" placeholder="如：小明" maxlength="10" required>
        </div>
        <div class="field">
          <label>邮箱</label>
          <input v-model="email" type="email" placeholder="请输入邮箱" required>
        </div>
        <div class="field">
          <label>密码</label>
          <input v-model="password" type="password" placeholder="请输入密码" required>
        </div>
        <div v-if="mode==='register'" class="field">
          <label>确认密码</label>
          <input v-model="confirmPassword" type="password" placeholder="再次输入密码" required>
        </div>
        <p v-if="error" class="error">{{ error }}</p>
        <button type="submit" class="big-btn primary start-btn" :disabled="loading">
          {{ loading ? '处理中...' : (mode==='login' ? '🚀 登录' : '🎉 注册') }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useProgressStore } from '../stores/progress'

const router = useRouter()
const userStore = useUserStore()
const progressStore = useProgressStore()

const mode = ref('login')
const name = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const error = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''
  if (mode.value === 'register') {
    if (!name.value.trim()) { error.value = '请输入孩子名字'; return }
    if (password.value.length < 4) { error.value = '密码至少4位'; return }
    if (password.value !== confirmPassword.value) { error.value = '两次密码不一致'; return }
  }
  if (!email.value.trim()) { error.value = '请输入邮箱'; return }
  if (!password.value) { error.value = '请输入密码'; return }

  loading.value = true
  try {
    if (mode.value === 'login') {
      await userStore.login(email.value, password.value)
    } else {
      await userStore.register(name.value, email.value, password.value)
    }
    await progressStore.loadProgress(userStore.userId)
    router.push('/menu')
  } catch (e) {
    const msg = e.response?.data?.message || e.message || '操作失败，请重试'
    error.value = msg
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  background: linear-gradient(180deg, #E0F7FA 0%, #FFF8E7 50%, #FFF0F0 100%);
  justify-content: center;
  align-items: center;
}
.login-box {
  display: flex; flex-direction: column; align-items: center;
  background: #fff; border-radius: 24px; padding: 30px 24px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.1); max-width: 360px; width: 90%;
  margin: 0 auto;
}
.login-box .dino-login { font-size: 64px; animation: dinoBounce 2s ease-in-out infinite; }
.login-box h1 { font-size: 28px; margin: 8px 0 4px; color: var(--text); }
.login-box .subtitle { font-size: 14px; color: #aaa; margin-bottom: 16px; }
.login-box .tab-bar { display: flex; gap: 0; margin-bottom: 16px; width: 100%; }
.login-box .tab-bar button {
  flex: 1; padding: 10px; border: none; font-size: 16px; font-family: inherit;
  background: #f5f5f5; cursor: pointer; transition: all 0.2s;
  border-radius: 10px 10px 0 0; font-weight: bold;
}
.login-box .tab-bar button.active { background: #fff; color: var(--secondary); border-bottom: 3px solid var(--secondary); }
.login-box .login-form { width: 100%; display: flex; flex-direction: column; gap: 12px; }
.login-box .login-form .field { display: flex; flex-direction: column; gap: 4px; }
.login-box .login-form .field label { font-size: 14px; color: #666; }
.login-box .login-form .field input {
  padding: 10px 14px; border: 2px solid #eee; border-radius: 12px;
  font-size: 16px; font-family: inherit; transition: border 0.2s; outline: none;
}
.login-box .login-form .field input:focus { border-color: var(--secondary); }
.login-box .login-form .error { color: var(--primary); font-size: 14px; text-align: center; margin: 0; }
.login-box .login-form .start-btn { margin-top: 4px; }
</style>
