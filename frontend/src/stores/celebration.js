import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useCelebrationStore = defineStore('celebration', () => {
  const visible = ref(false)
  const emoji = ref('🎉')
  const title = ref('太棒了！')
  const subtitle = ref('')

  function show(e, t, s) {
    emoji.value = e || '🎉'
    title.value = t || '太棒了！'
    subtitle.value = s || ''
    visible.value = true
  }

  function hide() {
    visible.value = false
  }

  return { visible, emoji, title, subtitle, show, hide }
})
