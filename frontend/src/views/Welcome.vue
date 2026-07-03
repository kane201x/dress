<template>
  <div class="page welcome">
    <div class="dino-big" @click="dinoClick">🦕</div>
    <h1>Dino 学习乐园</h1>
    <p class="subtitle">和 Dino 一起学数学、英语、古诗吧！</p>
    <div class="name-input-area">
      <label>你叫什么名字呀？</label>
      <input v-model="childName" placeholder="点这里输入" maxlength="6" @keyup.enter="goMenu" ref="nameInput">
      <div class="quick-names">
        <button v-for="n in quickNames" :key="n" @click="pickName(n)">{{ n }}</button>
      </div>
      <div class="diff-label">选择难度</div>
      <div class="diff-selector">
        <button v-for="(dl,i) in diffLabels" :key="i" :class="{active: difficulty===i+1}" @click="difficulty=i+1">{{ diffEmojis[i] }} {{ dl }}</button>
      </div>
      <button class="big-btn primary start-btn" @click="goMenu">🚀 开始学习！</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useCelebrationStore } from '../stores/celebration'
import { speakCN, unlockAudio } from '../utils/tts'

const router = useRouter()
const userStore = useUserStore()
const celebrationStore = useCelebrationStore()

const childName = ref('')
const nameInput = ref(null)
const quickNames = ['小明', '小红', '乐乐', '朵朵', '豆豆', '可乐']
const diffLabels = ['简单', '复杂', '困难', '超级困难']
const diffEmojis = ['🌱', '🌿', '🔥', '💎']
const difficulty = ref(1)
const dinoClicks = ref(0)

function pickName(n) { childName.value = n }

async function goMenu() {
  if (!childName.value.trim()) childName.value = '小宝贝'
  await userStore.login(childName.value)
  userStore.difficulty = difficulty.value
  unlockAudio()
  speakCN('你好' + childName.value + '，我们一起学习吧！')
  router.push('/menu')
}

function dinoClick() {
  dinoClicks.value++
  if (dinoClicks.value >= 5) {
    dinoClicks.value = 0
    userStore.updateStars(1)
    celebrationStore.show('🦕', 'Dino 喜欢你！', '送你一颗星星 ⭐')
  }
}

onMounted(() => { if (nameInput.value) nameInput.value.focus() })
</script>

<style scoped>
.welcome {
  background: linear-gradient(180deg, #FFF8E7 0%, #FFEED9 100%);
  gap: 10px;
}
.welcome .dino-big {
  font-size: 120px;
  animation: dinoBounce 1.5s ease-in-out infinite;
  cursor: pointer;
  filter: drop-shadow(0 8px 24px rgba(255, 107, 107, 0.3));
}
.welcome .dino-big:active { animation: dinoSpin 0.5s ease; }
.welcome h1 {
  font-size: 40px;
  color: var(--primary);
  text-shadow: 3px 3px 0 rgba(0, 0, 0, 0.06);
  margin-top: 4px;
}
.welcome .subtitle { font-size: 20px; color: #888; margin-bottom: 12px; }
.welcome .name-input-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
  margin-top: 8px;
}
.welcome .name-input-area label { font-size: 22px; color: #666; }
.welcome .name-input-area input {
  font-size: 32px;
  padding: 14px 28px;
  border: 4px solid var(--yellow);
  border-radius: 50px;
  text-align: center;
  font-family: inherit;
  background: #fff;
  outline: none;
  width: 240px;
  color: var(--text);
}
.welcome .name-input-area input:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 4px rgba(255, 107, 107, 0.2);
}
.welcome .quick-names {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: center;
}
.welcome .quick-names button {
  padding: 10px 24px;
  border-radius: 40px;
  border: 3px solid var(--mint);
  background: #fff;
  font-size: 24px;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.2s;
}
.welcome .quick-names button:active { background: var(--mint); transform: scale(0.95); }
</style>
