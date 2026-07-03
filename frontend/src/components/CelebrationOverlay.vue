<template>
  <div v-if="visible" class="celebration-overlay" @click.self="close">
    <div class="celebration-box">
      <div class="big">{{ emoji }}</div>
      <div class="msg">{{ title }}</div>
      <div class="sub">{{ subtitle }}</div>
      <button class="big-btn primary" @click="close">🥳 太好了！</button>
    </div>
    <div class="confetti-container" ref="confettiRef"></div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import { playCelebrationSound } from '../utils/tts'

const props = defineProps({
  visible: { type: Boolean, default: false },
  emoji: { type: String, default: '🎉' },
  title: { type: String, default: '太棒了！' },
  subtitle: { type: String, default: '' }
})

const emit = defineEmits(['close'])
const confettiRef = ref(null)

function close() { emit('close') }

function fireConfetti() {
  nextTick(() => {
    const box = confettiRef.value
    if (!box) return
    box.innerHTML = ''
    const colors = ['#FF6B6B', '#4ECDC4', '#FFE66D', '#A78BFA', '#FB923C', '#F472B6', '#95E1D3']
    for (let i = 0; i < 80; i++) {
      const el = document.createElement('div')
      el.className = 'confetti-piece'
      el.style.left = Math.random() * 100 + '%'
      el.style.background = colors[Math.floor(Math.random() * colors.length)]
      el.style.width = (6 + Math.random() * 10) + 'px'
      el.style.height = (6 + Math.random() * 10) + 'px'
      el.style.borderRadius = Math.random() > 0.5 ? '50%' : '2px'
      el.style.animationDuration = (1.5 + Math.random() * 2) + 's'
      el.style.animationDelay = (Math.random() * 1.5) + 's'
      box.appendChild(el)
    }
    setTimeout(() => { box.innerHTML = '' }, 5000)
  })
  playCelebrationSound()
}

watch(() => props.visible, (val) => { if (val) fireConfetti() })
</script>
