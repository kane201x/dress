import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getProgress, completeModule as apiCompleteModule, unlockSticker as apiUnlockSticker } from '../services/api'
import { useUserStore } from './user'

export const useProgressStore = defineStore('progress', () => {
  const completedModules = ref({})
  const stickers = ref([
    { emoji: '🔢', name: '数字小达人', unlocked: false, key: 'numbers' },
    { emoji: '🐱', name: '数数高手', unlocked: false, key: 'counting' },
    { emoji: '🔵', name: '形状大师', unlocked: false, key: 'shapes' },
    { emoji: '🐘', name: '比较大王', unlocked: false, key: 'compare' },
    { emoji: '📊', name: '排序小能手', unlocked: false, key: 'sorting' },
    { emoji: '➕', name: '加法小天才', unlocked: false, key: 'addition' },
    { emoji: '🔤', name: '字母小达人', unlocked: false, key: 'alphabet' },
    { emoji: '🎨', name: '色彩大师', unlocked: false, key: 'colors' },
    { emoji: '🐶', name: '动物专家', unlocked: false, key: 'animals' },
    { emoji: '🍎', name: '水果大王', unlocked: false, key: 'fruits' },
    { emoji: '🚗', name: '交通小专家', unlocked: false, key: 'vehicles' },
    { emoji: '👶', name: '身体小博士', unlocked: false, key: 'body' },
    { emoji: '📜', name: '古诗小才子', unlocked: false, key: 'poemAll' },
    { emoji: '⭐', name: '星星收集家', unlocked: false, key: 'starAll' },
  ])
  const maxStars = 50

  const mathDoneCount = computed(() =>
    ['numbers', 'counting', 'shapes', 'compare', 'sorting', 'addition'].filter(k => completedModules.value[k]).length
  )
  const engDoneCount = computed(() =>
    ['alphabet', 'colors', 'animals', 'fruits', 'vehicles', 'body'].filter(k => completedModules.value[k]).length
  )
  const poetryDoneCount = computed(() => {
    let count = 0
    for (let i = 0; i < 150; i++) { if (completedModules.value['poem' + i]) count++ }
    return count
  })

  function loadFromStorage() {
    try {
      const saved = localStorage.getItem('completedModules')
      if (saved) completedModules.value = JSON.parse(saved)
      const stickerSaved = localStorage.getItem('stickers')
      if (stickerSaved) {
        const parsed = JSON.parse(stickerSaved)
        stickers.value.forEach((s, i) => { if (parsed[i]) s.unlocked = parsed[i].unlocked })
      }
    } catch (e) {}
  }

  function persistModules() {
    localStorage.setItem('completedModules', JSON.stringify(completedModules.value))
  }

  function persistStickers() {
    localStorage.setItem('stickers', JSON.stringify(stickers.value.map(s => ({ unlocked: s.unlocked }))))
  }

  async function loadProgress(userId) {
    loadFromStorage()
    try {
      const res = await getProgress(userId)
      if (res.data) {
        const data = res.data
        if (data.completedModules) {
          Object.assign(completedModules.value, data.completedModules)
          persistModules()
        }
        if (data.stickers) {
          data.stickers.forEach(st => {
            const found = stickers.value.find(s => s.key === st.key)
            if (found) found.unlocked = st.unlocked
          })
          persistStickers()
        }
      }
    } catch (e) {}
  }

  async function completeModule(moduleKey) {
    completedModules.value[moduleKey] = true
    persistModules()
    const userStore = useUserStore()
    try {
      await apiCompleteModule(userStore.userId, moduleKey)
    } catch (e) {}
  }

  async function unlockSticker(key) {
    const sticker = stickers.value.find(s => s.key === key)
    if (sticker) {
      sticker.unlocked = true
      persistStickers()
      try {
        const userStore = useUserStore()
        await apiUnlockSticker(userStore.userId, key)
      } catch (e) {}
    }
  }

  return { completedModules, stickers, maxStars, mathDoneCount, engDoneCount, poetryDoneCount, loadProgress, completeModule, unlockSticker, loadFromStorage }
})
