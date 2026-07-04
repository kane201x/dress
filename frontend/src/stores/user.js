import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as apiLogin, register as apiRegister, updateDifficulty as apiUpdateDifficulty, updateStars as apiUpdateStars } from '../services/api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(localStorage.getItem('userId') || '')
  const userName = ref(localStorage.getItem('userName') || '')
  const userEmail = ref(localStorage.getItem('userEmail') || '')
  const stars = ref(Number(localStorage.getItem('stars') || 0))
  const difficulty = ref(Number(localStorage.getItem('difficulty') || 1))
  const isLoggedIn = computed(() => !!token.value)

  function persist() {
    localStorage.setItem('token', token.value)
    localStorage.setItem('userId', userId.value)
    localStorage.setItem('userName', userName.value)
    localStorage.setItem('userEmail', userEmail.value)
    localStorage.setItem('stars', String(stars.value))
    localStorage.setItem('difficulty', String(difficulty.value))
  }

  function clearAuth() {
    token.value = ''
    userId.value = ''
    userName.value = ''
    userEmail.value = ''
    stars.value = 0
    difficulty.value = 1
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('userName')
    localStorage.removeItem('userEmail')
    localStorage.removeItem('stars')
    localStorage.removeItem('difficulty')
  }

  function localFallback(name, email) {
    token.value = 'local-' + Date.now()
    userId.value = 'local-' + Date.now()
    userName.value = name || (email ? email.split('@')[0] : '小宝贝')
    userEmail.value = email || ''
    stars.value = 0
    difficulty.value = 1
    persist()
    return { token: token.value, user: { name: userName.value, email: userEmail.value, stars: 0, difficulty: 1 } }
  }

  async function login(email, password) {
    try {
      const res = await apiLogin(email, password)
      const data = res.data.data || res.data
      token.value = data.token
      userId.value = String(data.user.id)
      userName.value = data.user.name
      userEmail.value = data.user.email || email
      stars.value = data.user.stars || 0
      difficulty.value = data.user.difficulty || 1
      persist()
      return data
    } catch (e) {
      if (e.response && e.response.status !== 404) throw e
      return localFallback(null, email)
    }
  }

  async function register(name, email, password) {
    try {
      const res = await apiRegister(name, email, password)
      const data = res.data.data || res.data
      token.value = data.token
      userId.value = String(data.user.id)
      userName.value = data.user.name
      userEmail.value = data.user.email || email
      stars.value = data.user.stars || 0
      difficulty.value = data.user.difficulty || 1
      persist()
      return data
    } catch (e) {
      if (e.response && e.response.status !== 404) throw e
      return localFallback(name, email)
    }
  }

  function logout() {
    clearAuth()
  }

  async function updateDifficulty(level) {
    difficulty.value = level
    persist()
    try {
      await apiUpdateDifficulty(userId.value, level)
    } catch (e) {}
  }

  async function updateStars(delta) {
    stars.value = Math.min(stars.value + delta, 50)
    persist()
    try {
      await apiUpdateStars(userId.value, delta)
    } catch (e) {}
  }

  return { token, userId, userName, userEmail, stars, difficulty, isLoggedIn, login, register, logout, updateDifficulty, updateStars }
})
