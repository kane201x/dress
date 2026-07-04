<template>
  <div class="page welcome" @click="goLogin">
    <div class="dino-big" @click.stop="dinoClick">🦕</div>
    <h1>Dino 学习乐园</h1>
    <p class="subtitle">和 Dino 一起学数学、英语、古诗吧！</p>
    <button class="big-btn primary start-btn" @click.stop="goLogin">🚀 开始学习</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useCelebrationStore } from '../stores/celebration'

const router = useRouter()
const userStore = useUserStore()
const celebrationStore = useCelebrationStore()

const dinoClicks = ref(0)

function goLogin() {
  router.push('/login')
}

function dinoClick() {
  dinoClicks.value++
  if (dinoClicks.value >= 5) {
    dinoClicks.value = 0
    userStore.updateStars(1)
    celebrationStore.show('🦕', 'Dino 喜欢你！', '送你一颗星星 ⭐')
  }
}
</script>

<style scoped>
.welcome {
  background: linear-gradient(180deg, #E0F7FA 0%, #FFF8E7 50%, #FFF0F0 100%);
  justify-content: center;
  align-items: center;
  gap: 10px;
}
.welcome .dino-big { font-size: 120px; animation: dinoBounce 2s ease-in-out infinite; cursor: pointer; }
.welcome h1 { font-size: 36px; color: var(--text); margin: 0; }
.welcome .subtitle { font-size: 18px; color: #888; margin: 0; }
.welcome .start-btn { margin-top: 16px; }
</style>
