<template>
  <div class="page number-module">
    <TopBar back-route="/math" @back="router.push('/math')" />
    <DinoGuide text="点数字听发音" />
    <div class="number-display">
      <div class="digit" :class="{'digit-anim': digitAnim}" @click="speakNumber">{{ numData.digit }}</div>
      <div class="items-row">
        <span v-for="(e,i) in numData.items" :key="i" :style="{animationDelay: ((i%10)*0.04)+'s'}">{{ e }}</span>
      </div>
      <div class="word">—— {{ numData.word }} ——</div>
    </div>
    <button class="speak-btn" @click="speakNumber">🔊 听一听</button>
    <div class="progress" style="font-size:16px;color:#bbb;margin-top:6px;">{{ numIndex+1 }} / {{ numberList.length }}</div>
    <div class="nav-row">
      <button @click="prevNumber" :disabled="numIndex === 0">⬅️</button>
      <button @click="nextNumber" :disabled="numIndex === numberList.length-1">➡️</button>
    </div>
    <div v-if="numIndex === numberList.length-1" class="complete-hint" @click="completeNumber">🎉 领星星</div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { useProgressStore } from '../../stores/progress'
import { useCelebrationStore } from '../../stores/celebration'
import { speakCN } from '../../utils/tts'
import TopBar from '../../components/TopBar.vue'
import DinoGuide from '../../components/DinoGuide.vue'

const router = useRouter()
const userStore = useUserStore()
const progressStore = useProgressStore()
const celebrationStore = useCelebrationStore()

const numEmojis = ['🍎', '🐱', '🌸', '⭐', '🍭', '🦋', '🌻', '🐦', '🍊', '🚗', '🎈', '🐰', '🍓', '🌺', '🐶']
const numWords = ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十',
  '十一', '十二', '十三', '十四', '十五', '十六', '十七', '十八', '十九', '二十',
  '二十一', '二十二', '二十三', '二十四', '二十五', '二十六', '二十七', '二十八', '二十九', '三十',
  '三十一', '三十二', '三十三', '三十四', '三十五', '三十六', '三十七', '三十八', '三十九', '四十',
  '四十一', '四十二', '四十三', '四十四', '四十五', '四十六', '四十七', '四十八', '四十九', '五十'
]

function pick(arr) { return arr[Math.floor(Math.random() * arr.length)] }

const numberList = computed(() => {
  const qty = 110
  const result = []
  for (let i = 0; i < qty; i++) {
    const n = i + 1
    const e = pick(numEmojis)
    result.push({
      digit: String(n),
      word: n <= 50 ? numWords[n - 1] : String(n),
      items: Array.from({ length: n }, () => e)
    })
  }
  return result
})

const numIndex = ref(0)
const digitAnim = ref(false)
const numData = computed(() => numberList.value[numIndex.value])

function speakNumber() {
  digitAnim.value = true
  setTimeout(() => digitAnim.value = false, 400)
  speakCN(numData.value.digit)
}

function prevNumber() {
  if (numIndex.value > 0) {
    numIndex.value--
    digitAnim.value = true
    setTimeout(() => digitAnim.value = false, 400)
    nextTick(() => speakNumber())
  }
}

function nextNumber() {
  if (numIndex.value < numberList.value.length - 1) {
    numIndex.value++
    digitAnim.value = true
    setTimeout(() => digitAnim.value = false, 400)
    nextTick(() => speakNumber())
  }
}

async function completeNumber() {
  if (!progressStore.completedModules.numbers) {
    await progressStore.completeModule('numbers')
    await userStore.updateStars(3)
    await progressStore.unlockSticker('numbers')
    celebrationStore.show('🔢', '数字小达人！', '你认识了所有数字！')
  } else {
    celebrationStore.show('⭐', '已经完成啦！', '你真厉害！')
  }
}
</script>

<style scoped>
.number-module {
  background: linear-gradient(180deg, #FFF0F0 0%, #FFF8E7 100%);
  padding-top: 70px;
  justify-content: flex-start;
}
.number-module .number-display { display: flex; flex-direction: column; align-items: center; gap: 4px; margin-top: 10px; }
.number-module .number-display .digit {
  font-size: 120px; font-weight: bold; color: var(--primary); line-height: 1;
  text-shadow: 4px 4px 0 rgba(255, 107, 107, 0.2); transition: all 0.3s; cursor: pointer;
}
.number-module .number-display .digit:active { transform: scale(1.1); }
.number-module .number-display .digit-anim { animation: numberPop 0.4s cubic-bezier(.34, 1.56, .64, 1); }
.number-module .number-display .items-row {
  display: flex; flex-wrap: wrap; font-size: 36px; min-height: 60px;
  justify-content: flex-start; margin-top: 4px; max-width: 400px;
  margin-left: auto; margin-right: auto;
}
.number-module .number-display .items-row span {
  width: 10%; flex: 0 0 10%; text-align: center;
  animation: itemFadeIn 0.25s ease both; line-height: 1.5;
}
.number-module .number-display .word { font-size: 28px; color: #888; margin-top: 2px; }
.number-module .nav-row { display: flex; gap: 30px; margin-top: 16px; }
.number-module .nav-row button {
  width: 64px; height: 64px; border-radius: 50%; border: none; font-size: 32px;
  background: #fff; box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1); cursor: pointer; transition: all 0.2s;
}
.number-module .nav-row button:active { transform: scale(0.9); }
.number-module .nav-row button:disabled { opacity: 0.3; }
.number-module .speak-btn {
  margin-top: 8px; padding: 12px 32px; border-radius: 40px; border: none;
  background: var(--secondary); color: #fff; font-size: 22px; font-family: inherit;
  cursor: pointer; box-shadow: 0 4px 0 rgba(0, 0, 0, 0.1); transition: all 0.1s;
}
.number-module .speak-btn:active { transform: translateY(3px); box-shadow: 0 1px 0 rgba(0, 0, 0, 0.1); }
.number-module .complete-hint {
  margin-top: 12px; padding: 8px 24px; border-radius: 30px;
  background: var(--yellow); font-size: 18px; font-weight: bold;
  cursor: pointer; animation: pulse 1.5s ease-in-out infinite;
}
</style>
