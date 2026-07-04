<template>
  <div class="page counting-module">
    <TopBar back-route="/math" @back="router.push('/math')" />
    <DinoGuide text="点点小图标数一数" />
    <div class="scene">
      <div class="question">{{ countData.question }}</div>
      <div class="objects" :class="'cols-' + gridCols">
        <div v-for="(obj,i) in countData.objects" :key="i"
          class="obj" :class="{counted: countData.counted[i]}"
          @click="countObj(i)"
          :style="objStyle(countData.total)">
          {{ obj }}
        </div>
      </div>
      <div class="counter">
        <span class="num" :class="{'pop': countPop}">{{ countData.current }}</span>
        <span v-if="countData.done"> / {{ countData.total }} ✅</span>
        <span v-else> / {{ countData.total }}</span>
      </div>
      <div v-if="countData.done" class="result">🎉 一共 {{ countData.total }} 个！</div>
      <div class="actions">
        <button class="big-btn outline" @click="skipCount">⏭️ 跳过</button>
        <button v-if="countData.done && countIndex < countScenes.length-1" class="big-btn secondary" @click="nextCount">➡️ 下一题</button>
        <button v-if="countData.done && countIndex === countScenes.length-1" class="big-btn yellow" @click="completeCounting">⭐ 领奖励</button>
      </div>
    </div>
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

const gridCols = computed(() => countData.total >= 10 ? 10 : countData.total)

function objStyle(total) {
  if (total > 80) return { fontSize: '18px', padding: '1px' }
  if (total > 50) return { fontSize: '22px', padding: '1px 2px' }
  if (total > 30) return { fontSize: '26px', padding: '1px 2px' }
  return { fontSize: '32px', padding: '2px 4px' }
}

function randInt(min, max) { return Math.floor(Math.random() * (max - min + 1)) + min }
function pick(arr) { return arr[Math.floor(Math.random() * arr.length)] }

const countScenes = computed(() => {
  const d = userStore.difficulty
  const qty = 50
  let minCount, maxCount
  if (d === 1) { minCount = 2; maxCount = 5 }
  else if (d === 2) { minCount = 3; maxCount = 10 }
  else if (d === 3) { minCount = 5; maxCount = 20 }
  else if (d === 4) { minCount = 8; maxCount = 40 }
  else { minCount = 10; maxCount = 100 }
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

function skipCount() {
  if (countIndex.value < countScenes.length - 1) {
    countIndex.value++
    initCount(countIndex.value)
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
.counting-module .scene { display: flex; flex-direction: column; align-items: center; gap: 8px; margin-top: 10px; width: 100%; padding: 0 4px; }
.counting-module .scene .question { font-size: 24px; color: #666; }
.counting-module .scene .objects {
  display: grid; gap: 2px; justify-items: center; align-items: center;
  padding: 6px; min-height: 50px; max-width: 420px; width: 100%;
}
.counting-module .scene .objects.cols-10 { grid-template-columns: repeat(10, 1fr); }
.counting-module .scene .objects .obj {
  cursor: pointer; transition: all 0.15s; user-select: none;
  border-radius: 8px; background: rgba(255, 255, 255, 0.5); line-height: 1;
  width: 100%; text-align: center;
}
.counting-module .scene .objects .obj:active { transform: scale(1.3); }
.counting-module .scene .objects .obj.counted { opacity: 0.25; transform: scale(0.75); }
.counting-module .scene .counter { font-size: 40px; font-weight: bold; color: var(--secondary); margin-top: 4px; }
.counting-module .scene .counter .num.pop { animation: numberPop 0.3s ease; }
.counting-module .scene .result { font-size: 28px; font-weight: bold; color: var(--primary); animation: fadeInUp 0.5s ease; }
.counting-module .scene .actions { display: flex; gap: 10px; margin-top: 8px; align-items: center; }
.counting-module .scene .actions .big-btn.outline { background: transparent; border: 2px solid #ccc; color: #999; font-size: 15px; padding: 8px 18px; border-radius: 20px; cursor: pointer; }
.counting-module .scene .actions .big-btn.outline:active { background: #f5f5f5; }
</style>
