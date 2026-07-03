<template>
  <div class="page counting-module">
    <TopBar back-route="/math" @back="router.push('/math')" />
    <DinoGuide text="点点小图标数一数" />
    <div class="scene">
      <div class="question">{{ countData.question }}</div>
      <div class="objects" :class="{many: countData.total > 8, 'many-many': countData.total > 14}">
        <div v-for="(obj,i) in countData.objects" :key="i" class="obj" :class="{counted: countData.counted[i]}" @click="countObj(i)">
          {{ obj }}
        </div>
      </div>
      <div class="counter">
        <span class="num" :class="{'pop': countPop}">{{ countData.current }}</span>
        <span v-if="countData.done"> / {{ countData.total }} ✅</span>
      </div>
      <div v-if="countData.done" class="result">🎉 一共 {{ countData.total }} 个！</div>
    </div>
    <button v-if="countData.done && countIndex < countScenes.length-1" class="big-btn secondary" style="margin-top:10px" @click="nextCount">➡️ 下一题</button>
    <button v-if="countData.done && countIndex === countScenes.length-1" class="big-btn yellow" style="margin-top:10px" @click="completeCounting">⭐ 领奖励</button>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
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

const countEmojis = ['🐱', '🍎', '🌻', '🐦', '🦋', '🐶', '🌸', '⭐', '🐰', '🍭', '🚗', '🎈', '🐟', '🍓', '🌺']
const countIndex = ref(0)
const countPop = ref(false)
const countData = reactive({ question: '', objects: [], total: 0, current: 0, counted: [], done: false })

function randInt(min, max) { return Math.floor(Math.random() * (max - min + 1)) + min }
function pick(arr) { return arr[Math.floor(Math.random() * arr.length)] }

const countScenes = computed(() => {
  const d = userStore.difficulty
  const qty = 50
  const minCount = d === 1 ? 2 : d === 2 ? 4 : d === 3 ? 6 : 8
  const maxCount = d === 1 ? 5 : d === 2 ? 8 : d === 3 ? 12 : 18
  const scenes = []
  for (let i = 0; i < qty; i++) {
    const total = randInt(minCount, maxCount)
    const e = pick(countEmojis)
    scenes.push({
      question: e + ' 有几个？',
      objects: Array.from({ length: total }, () => e),
      total
    })
  }
  return scenes
})

function initCount(idx) {
  const s = countScenes.value[idx]
  countData.question = s.question
  countData.objects = [...s.objects]
  countData.total = s.total
  countData.current = 0
  countData.counted = Array(s.objects.length).fill(false)
  countData.done = false
}

function countObj(i) {
  if (countData.done || countData.counted[i]) return
  countData.counted[i] = true
  countData.current++
  countPop.value = true
  setTimeout(() => countPop.value = false, 300)
  speakCN(String(countData.current))
  if (countData.current === countData.total) {
    countData.done = true
    setTimeout(() => speakCN('一共' + countData.total + '个！'), 400)
  }
}

function nextCount() { countIndex.value++; initCount(countIndex.value) }

async function completeCounting() {
  if (!progressStore.completedModules.counting) {
    await progressStore.completeModule('counting')
    await userStore.updateStars(3)
    await progressStore.unlockSticker('counting')
    celebrationStore.show('🐱', '数数高手！', '你会数数啦！')
  } else { celebrationStore.show('⭐', '已经完成啦！', '') }
}

initCount(0)
</script>

<style scoped>
.counting-module {
  background: linear-gradient(180deg, #F0FFF4 0%, #FFF8E7 100%);
  padding-top: 70px;
  justify-content: flex-start;
}
.counting-module .scene { display: flex; flex-direction: column; align-items: center; gap: 10px; margin-top: 10px; }
.counting-module .scene .question { font-size: 24px; color: #666; }
.counting-module .scene .objects {
  display: flex; gap: 6px; flex-wrap: wrap; justify-content: center;
  padding: 12px; min-height: 80px; max-width: 100%; overflow-y: auto;
}
.counting-module .scene .objects .obj {
  font-size: 42px; cursor: pointer; transition: all 0.2s;
  padding: 4px 8px; border-radius: 14px; background: rgba(255, 255, 255, 0.6); flex-shrink: 0;
}
.counting-module .scene .objects.many .obj { font-size: 32px; padding: 2px 6px; }
.counting-module .scene .objects.many-many .obj { font-size: 24px; padding: 2px 4px; }
.counting-module .scene .objects .obj:active { transform: scale(1.2); }
.counting-module .scene .objects .obj.counted { opacity: 0.4; transform: scale(0.8); }
.counting-module .scene .counter { font-size: 48px; font-weight: bold; color: var(--secondary); }
.counting-module .scene .counter .num.pop { animation: numberPop 0.3s ease; }
.counting-module .scene .result { font-size: 28px; font-weight: bold; color: var(--primary); animation: fadeInUp 0.5s ease; }
</style>
