import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as apiLogin, updateDifficulty as apiUpdateDifficulty, updateStars as apiUpdateStars } from '../services/api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(localStorage.getItem('userId') || '')
  const userName = ref(localStorage.getItem('userName') || '')
  const stars = ref(Number(localStorage.getItem('stars') || 0))
  const difficulty = ref(Number(localStorage.getItem('difficulty') || 1))
  const isLoggedIn = computed(() => !!token.value)

  function persist() {
    localStorage.setItem('token', token.value)
    localStorage.setItem('userId', userId.value)
    localStorage.setItem('userName', userName.value)
    localStorage.setItem('stars', String(stars.value))
    localStorage.setItem('difficulty', String(difficulty.value))
  }

  async function login(name) {
    try {
      const res = await apiLogin(name)
      const data = res.data
      token.value = data.token
      userId.value = data.user._id || data.user.id
      userName.value = data.user.name || name
      stars.value = data.user.stars || 0
      difficulty.value = data.user.difficulty || 1
      persist()
      return data
    } catch (e) {
      token.value = 'local-' + Date.now()
      userId.value = 'local-' + Date.now()
      userName.value = name
      stars.value = 0
      difficulty.value = 1
      persist()
      return { token: token.value, user: { name, stars: 0, difficulty: 1 } }
    }
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

  return { token, userId, userName, stars, difficulty, isLoggedIn, login, updateDifficulty, updateStars }
})
