<template>
  <div class="page shape-module">
    <TopBar back-route="/math" @back="router.push('/math')" />
    <div class="shape-area">
      <DinoGuide text="找出一样的形状" />
      <div class="target" :style="{color: shapeData.color}">{{ shapeData.targetEmoji }}</div>
      <div class="hint">找到一样的形状吧！</div>
      <div class="options">
        <button v-for="(opt,i) in shapeData.options" :key="i"
          :class="{correct: shapeData.feedback==='correct' && shapeData.selected===i, wrong: shapeData.feedback==='wrong' && shapeData.selected===i}"
          @click="pickShape(i)">{{ opt }}</button>
      </div>
      <div class="feedback">{{ shapeData.feedbackText }}</div>
      <div class="progress" style="font-size:16px;color:#bbb;margin-top:6px;">{{ shapeIndex+1 }} / {{ shapeList.length }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, nextTick } from 'vue'
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

function shuffle(arr) {
  const a = [...arr]
  for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [a[i], a[j]] = [a[j], a[i]] }
  return a
}
function pick(arr) { return arr[Math.floor(Math.random() * arr.length)] }

const shapePool = [
  { emoji: '🔴', name: '圆形', color: '#FF6B6B' },
  { emoji: '🟦', name: '方形', color: '#4ECDC4' },
  { emoji: '🔺', name: '三角形', color: '#A78BFA' },
  { emoji: '⭐', name: '星形', color: '#FFE66D' },
  { emoji: '🟡', name: '心形', color: '#F472B6' },
  { emoji: '⬟', name: '五边形', color: '#FB923C' },
  { emoji: '🔷', name: '菱形', color: '#95E1D3' },
  { emoji: '🟩', name: '椭圆', color: '#2ED573' },
  { emoji: '🔶', name: '六边形', color: '#FFA502' },
  { emoji: '⬠', name: '月牙', color: '#F8BBD0' },
]

const shapeList = computed(() => {
  const qty = 50
  const d = userStore.difficulty
  const pool = d === 1 ? shapePool.slice(0, 5) : d === 2 ? shapePool.slice(0, 7) : shapePool
  const result = []
  for (let i = 0; i < qty; i++) {
    const target = pick(pool)
    const others = pool.filter(s => s.emoji !== target.emoji)
    const wrong = []
    const needed = d === 1 ? 2 : d === 2 ? 3 : 3
    const shuffledOthers = shuffle(others)
    for (let j = 0; j < needed && j < shuffledOthers.length; j++) wrong.push(shuffledOthers[j].emoji)
    const opts = shuffle([target.emoji, ...wrong])
    result.push({ targetEmoji: target.emoji, name: target.name, options: opts, color: target.color })
  }
  return result
})

const shapeIndex = ref(0)
const shapeData = reactive({ targetEmoji: '', color: '', options: [], selected: -1, feedback: '', feedbackText: '' })

function initShape(idx) {
  const s = shapeList.value[idx]
  shapeData.targetEmoji = s.targetEmoji
  shapeData.color = s.color
  shapeData.options = [...s.options]
  shapeData.selected = -1
  shapeData.feedback = ''
  shapeData.feedbackText = ''
}

function pickShape(i) {
  if (shapeData.feedback) return
  shapeData.selected = i
  const target = shapeList.value[shapeIndex.value]
  if (shapeData.options[i] === target.targetEmoji) {
    shapeData.feedback = 'correct'
    shapeData.feedbackText = '🎉 答对啦！'
    speakCN('对了！这是' + target.name)
    setTimeout(() => {
      if (shapeIndex.value < shapeList.value.length - 1) {
        shapeIndex.value++
        initShape(shapeIndex.value)
        nextTick(() => speakCN('找到一样的形状'))
      } else {
        if (!progressStore.completedModules.shapes) {
          progressStore.completeModule('shapes')
          userStore.updateStars(3)
          progressStore.unlockSticker('shapes')
          celebrationStore.show('🔵', '形状大师！', '你认识了所有形状！')
        } else { celebrationStore.show('⭐', '全部完成！', '') }
      }
    }, 1000)
  } else {
    shapeData.feedback = 'wrong'
    shapeData.feedbackText = '😅 再试试~'
    speakCN('再试试')
    setTimeout(() => { shapeData.feedback = ''; shapeData.feedbackText = ''; shapeData.selected = -1 }, 600)
  }
}

initShape(0)
</script>

<style scoped>
.shape-module {
  background: linear-gradient(180deg, #F3E8FF 0%, #FFF8E7 100%);
  padding-top: 70px;
  justify-content: flex-start;
}
.shape-module .shape-area { display: flex; flex-direction: column; align-items: center; gap: 14px; margin-top: 10px; width: 100%; }
.shape-module .shape-area .target { font-size: 100px; animation: dinoBounce 2s ease-in-out infinite; cursor: pointer; }
.shape-module .shape-area .hint { font-size: 20px; color: #888; }
.shape-module .shape-area .options { display: flex; gap: 20px; margin-top: 4px; }
.shape-module .shape-area .options button {
  font-size: 70px; width: 100px; height: 100px; border-radius: 24px;
  border: 4px solid transparent; background: #fff;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1); cursor: pointer;
  transition: all 0.2s; display: flex; align-items: center; justify-content: center;
}
.shape-module .shape-area .options button:active { transform: scale(0.9); }
.shape-module .shape-area .options button.correct { border-color: var(--secondary); background: #E8F8F5; animation: correctWiggle 0.4s ease; }
.shape-module .shape-area .options button.wrong { border-color: var(--primary); background: #FFF0F0; animation: shake 0.3s ease; }
.shape-module .shape-area .feedback { font-size: 32px; min-height: 48px; }
</style>
